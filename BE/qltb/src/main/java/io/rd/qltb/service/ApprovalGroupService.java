package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalGroup;
import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.events.BeforeDeleteApprovalGroup;
import io.rd.qltb.events.BeforeDeleteApprovalWorkflow;
import io.rd.qltb.model.ApprovalGroupDTO;
import io.rd.qltb.repos.ApprovalGroupRepository;
import io.rd.qltb.repos.ApprovalWorkflowRepository;
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
    private final ApplicationEventPublisher publisher;

    public ApprovalGroupService(final ApprovalGroupRepository approvalGroupRepository,
            final ApprovalWorkflowRepository approvalWorkflowRepository,
            final ApplicationEventPublisher publisher) {
        this.approvalGroupRepository = approvalGroupRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
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
        approvalGroupDTO.setGroupApprNameId(approvalGroup.getGroupApprNameId());
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

        return approvalGroupDTO;
    }

    private ApprovalGroup mapToEntity(final ApprovalGroupDTO approvalGroupDTO,
            final ApprovalGroup approvalGroup) {
        approvalGroup.setGroupApprNameId(approvalGroupDTO.getGroupApprNameId());
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
