package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalGroup;
import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.GroupApprovalName;
import io.rd.qltb.events.BeforeDeleteApprovalGroup;
import io.rd.qltb.events.BeforeDeleteApprovalWorkflow;
import io.rd.qltb.model.ApprovalGroupDTO;
import io.rd.qltb.repos.ApprovalGroupRepository;
import io.rd.qltb.repos.ApprovalWorkflowRepository;
import io.rd.qltb.repos.GroupApprovalNameRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ApprovalGroupService {

    private final ApprovalGroupRepository approvalGroupRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final GroupApprovalNameRepository groupApprovalNameRepository;
    private final ApplicationEventPublisher publisher;

    public ApprovalGroupService(final ApprovalGroupRepository approvalGroupRepository,
            final ApprovalWorkflowRepository approvalWorkflowRepository,
            final GroupApprovalNameRepository groupApprovalNameRepository,
            final ApplicationEventPublisher publisher) {
        this.approvalGroupRepository = approvalGroupRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.groupApprovalNameRepository = groupApprovalNameRepository;
        this.publisher = publisher;
    }

    public List<ApprovalGroupDTO> findAll() {
        final List<ApprovalGroup> approvalGroups = approvalGroupRepository.findAll(Sort.by("id"));
        return approvalGroups.stream()
                .map(approvalGroup -> mapToDTO(approvalGroup, new ApprovalGroupDTO()))
                .toList();
    }

    public ApprovalGroupDTO get(final Long id) {
        return approvalGroupRepository.findById(id)
                .map(approvalGroup -> mapToDTO(approvalGroup, new ApprovalGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ApprovalGroupDTO approvalGroupDTO) {
        final ApprovalGroup approvalGroup = new ApprovalGroup();
        mapToEntity(approvalGroupDTO, approvalGroup);
        return approvalGroupRepository.save(approvalGroup).getId();
    }

    public void update(final Long id, final ApprovalGroupDTO approvalGroupDTO) {
        final ApprovalGroup approvalGroup = approvalGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(approvalGroupDTO, approvalGroup);
        approvalGroupRepository.save(approvalGroup);
    }

    public void delete(final Long id) {
        final ApprovalGroup approvalGroup = approvalGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteApprovalGroup(id));
        approvalGroupRepository.delete(approvalGroup);
    }

    private ApprovalGroupDTO mapToDTO(final ApprovalGroup approvalGroup, final ApprovalGroupDTO approvalGroupDTO) {
        approvalGroupDTO.setId(approvalGroup.getId());
        approvalGroupDTO.setLevel(approvalGroup.getLevel());
        approvalGroupDTO.setIsRequired(approvalGroup.getIsRequired());
        approvalGroupDTO.setCreatedAt(approvalGroup.getCreatedAt());
        approvalGroupDTO.setUpdatedAt(approvalGroup.getUpdatedAt());
        approvalGroupDTO.setCreatedBy(approvalGroup.getCreatedBy());
        approvalGroupDTO.setUpdatedBy(approvalGroup.getUpdatedBy());
        approvalGroupDTO.setStatus(approvalGroup.getStatus());

        // Sao chép Workflow có kiểm soát
        if (approvalGroup.getWorkflow() != null) {
            ApprovalWorkflow workflowCopy = new ApprovalWorkflow();
            workflowCopy.setId(approvalGroup.getWorkflow().getId());
            workflowCopy.setCode(approvalGroup.getWorkflow().getCode());
            workflowCopy.setName(approvalGroup.getWorkflow().getName());
            workflowCopy.setStatus(approvalGroup.getWorkflow().getStatus());
            workflowCopy.setCreatedAt(approvalGroup.getWorkflow().getCreatedAt());
            workflowCopy.setUpdatedAt(approvalGroup.getWorkflow().getUpdatedAt());
            workflowCopy.setCreatedBy(approvalGroup.getWorkflow().getCreatedBy());
            workflowCopy.setUpdatedBy(approvalGroup.getWorkflow().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp hoặc dữ liệu dư thừa
            workflowCopy.setWorkflowApprovalGroups(null);

            approvalGroupDTO.setWorkflow(workflowCopy);
        } else {
            approvalGroupDTO.setWorkflow(null);
        }

        if (approvalGroup.getGroupApprovalName() != null) {
            GroupApprovalName groupNameCopy = new GroupApprovalName();
            groupNameCopy.setId(approvalGroup.getGroupApprovalName().getId());
            groupNameCopy.setCode(approvalGroup.getGroupApprovalName().getCode());
            groupNameCopy.setName(approvalGroup.getGroupApprovalName().getName());
            groupNameCopy.setDescription(approvalGroup.getGroupApprovalName().getDescription());
            groupNameCopy.setCreatedAt(approvalGroup.getGroupApprovalName().getCreatedAt());
            groupNameCopy.setUpdatedAt(approvalGroup.getGroupApprovalName().getUpdatedAt());
            groupNameCopy.setCreatedBy(approvalGroup.getGroupApprovalName().getCreatedBy());
            groupNameCopy.setUpdatedBy(approvalGroup.getGroupApprovalName().getUpdatedBy());
            groupNameCopy.setStatus(approvalGroup.getGroupApprovalName().getStatus());

            // tránh vòng lặp
            groupNameCopy.setApprovalGroups(null);

            approvalGroupDTO.setGroupApprovalName(groupNameCopy);
        } else {
            approvalGroupDTO.setGroupApprovalName(null);
        }

        return approvalGroupDTO;
    }

    private ApprovalGroup mapToEntity(final ApprovalGroupDTO approvalGroupDTO,
            final ApprovalGroup approvalGroup) {
        approvalGroup.setLevel(approvalGroupDTO.getLevel());
        approvalGroup.setIsRequired(approvalGroupDTO.getIsRequired());
        approvalGroup.setCreatedAt(approvalGroupDTO.getCreatedAt());
        approvalGroup.setUpdatedAt(approvalGroupDTO.getUpdatedAt());
        approvalGroup.setCreatedBy(approvalGroupDTO.getCreatedBy());
        approvalGroup.setUpdatedBy(approvalGroupDTO.getUpdatedBy());
        approvalGroup.setStatus(approvalGroupDTO.getStatus());
        final ApprovalWorkflow workflow = approvalGroupDTO.getWorkflow() == null ? null : approvalWorkflowRepository.findById(approvalGroupDTO.getWorkflow().getId())
                .orElseThrow(() -> new NotFoundException("workflow not found"));
        approvalGroup.setWorkflow(workflow);

        final GroupApprovalName groupApprovalName = approvalGroupDTO.getGroupApprovalName() == null ? null :
                groupApprovalNameRepository.findById(approvalGroupDTO.getGroupApprovalName().getId())
                        .orElseThrow(() -> new NotFoundException("groupApprovalName not found"));
        approvalGroup.setGroupApprovalName(groupApprovalName);
        return approvalGroup;
    }

    @EventListener(BeforeDeleteApprovalWorkflow.class)
    public void on(final BeforeDeleteApprovalWorkflow event) {
        final ReferencedException referencedException = new ReferencedException();
        final ApprovalGroup workflowApprovalGroup = approvalGroupRepository.findFirstByWorkflowId(event.getId());
        if (workflowApprovalGroup != null) {
            referencedException.setKey("approvalWorkflow.approvalGroup.workflow.referenced");
            referencedException.addParam(workflowApprovalGroup.getId());
            throw referencedException;
        }
    }

}
