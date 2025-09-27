package io.rd.qltb.service;

import io.rd.qltb.domain.Approval;
import io.rd.qltb.domain.ApprovalGroup;
import io.rd.qltb.domain.ApprovalGroupUser;
import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.model.ApprovalDTO;
import io.rd.qltb.model.ApprovalRequestDTO;
import io.rd.qltb.repos.ApprovalRepository;
import io.rd.qltb.repos.ApprovalWorkflowRepository;
import io.rd.qltb.util.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;

    public ApprovalService(final ApprovalRepository approvalRepository, final ApprovalWorkflowRepository approvalWorkflowRepository) {
        this.approvalRepository = approvalRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
    }

    public List<ApprovalDTO> findAll() {
        final List<Approval> approvals = approvalRepository.findAll(Sort.by("id"));
        return approvals.stream()
                .map(approval -> mapToDTO(approval, new ApprovalDTO()))
                .toList();
    }

    public ApprovalDTO get(final Long id) {
        return approvalRepository.findById(id)
                .map(approval -> mapToDTO(approval, new ApprovalDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ApprovalDTO approvalDTO) {
        final Approval approval = new Approval();
        mapToEntity(approvalDTO, approval);
        return approvalRepository.save(approval).getId();
    }

    public void update(final Long id, final ApprovalDTO approvalDTO) {
        final Approval approval = approvalRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(approvalDTO, approval);
        approvalRepository.save(approval);
    }

    public void delete(final Long id) {
        final Approval approval = approvalRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        approvalRepository.delete(approval);
    }

    public void createScriptApproval(ApprovalRequestDTO approvalRequestDTO, String entityType, String userName){
        ApprovalWorkflow approvalWorkflow = approvalWorkflowRepository.findById(approvalRequestDTO.getWorkflowId()).orElseThrow(()-> new NotFoundException("approvalWorkflow not found"));
        for(ApprovalGroup approvalGroup: approvalWorkflow.getWorkflowApprovalGroups()){
            for (ApprovalGroupUser approvalGroupUser:approvalGroup.getGroupApprovalGroupUsers()){
                Approval approval = new Approval();
                approval.setEntityId(approvalRequestDTO.getEntityId());
                approval.setEntityType(entityType);
                approval.setUserApproval(approvalGroupUser);
                approval.setStatus(1);
                approval.setCreatedAt(LocalDateTime.now());
                approval.setUpdatedAt(LocalDateTime.now());
                approval.setCreatedBy(userName);
                approval.setGroup(approvalGroup);
                approval.setWorkflow(approvalWorkflow);
                approvalRepository.save(approval);
            }
        }
    }

    public ApprovalDTO mapToDTO(final Approval approval, final ApprovalDTO approvalDTO) {
        approvalDTO.setId(approval.getId());
        approvalDTO.setEntityType(approval.getEntityType());
        approvalDTO.setEntityId(approval.getEntityId());
        approvalDTO.setUserApproval(approval.getUserApproval());
        approvalDTO.setWorkflow(approval.getWorkflow());
        approvalDTO.setGroup(approval.getGroup());
        approvalDTO.setStatus(approval.getStatus());
        approvalDTO.setSignedAt(approval.getSignedAt());
        approvalDTO.setNote(approval.getNote());
        approvalDTO.setCreatedAt(approval.getCreatedAt());
        approvalDTO.setUpdatedAt(approval.getUpdatedAt());
        approvalDTO.setCreatedBy(approval.getCreatedBy());
        approvalDTO.setUpdatedBy(approval.getUpdatedBy());
        // Sao chép ApprovalGroup có kiểm soát
        if (approval.getGroup() != null) {
            ApprovalGroup groupCopy = new ApprovalGroup();
            groupCopy.setId(approval.getGroup().getId());
            groupCopy.setLevel(approval.getGroup().getLevel());
            groupCopy.setIsRequired(approval.getGroup().getIsRequired());
            groupCopy.setCreatedAt(approval.getGroup().getCreatedAt());
            groupCopy.setUpdatedAt(approval.getGroup().getUpdatedAt());
            groupCopy.setCreatedBy(approval.getGroup().getCreatedBy());
            groupCopy.setUpdatedBy(approval.getGroup().getUpdatedBy());
            groupCopy.setStatus(approval.getGroup().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            groupCopy.setWorkflow(null);
            groupCopy.setGroupApprovalGroupUsers(null);
            groupCopy.setGroupApprovalName(null);

            approvalDTO.setGroup(groupCopy);
        } else {
            approvalDTO.setGroup(null);
        }
        // Sao chép ApprovalGroupUser có kiểm soát
        if(approval.getUserApproval() != null) {
            ApprovalGroupUser approvalGroupUserCopy = new ApprovalGroupUser();
            approvalGroupUserCopy.setId(approval.getUserApproval().getId());
            approvalGroupUserCopy.setUsername(approval.getUserApproval().getUsername());
            approvalGroupUserCopy.setStatus(approval.getUserApproval().getStatus());
            approvalGroupUserCopy.setTimeSign(approval.getUserApproval().getTimeSign());
            approvalGroupUserCopy.setCreatedAt(approval.getUserApproval().getCreatedAt());
            approvalGroupUserCopy.setUpdatedAt(approval.getUserApproval().getUpdatedAt());
            // Xóa các quan hệ con để tránh vòng lặp
            approvalGroupUserCopy.setGroup(null);
            approvalDTO.setUserApproval(approvalGroupUserCopy);
        } else {
            approvalDTO.setUserApproval(null);
        }
        // Sao chép WorkFlow có kiểm soát
        if (approval.getWorkflow() != null){
            ApprovalWorkflow workflowCopy = new ApprovalWorkflow();
            workflowCopy.setId(approval.getWorkflow().getId());
            workflowCopy.setCode(approval.getWorkflow().getCode());
            workflowCopy.setName(approval.getWorkflow().getName());
            workflowCopy.setDescription(approval.getWorkflow().getDescription());
            workflowCopy.setCreatedAt(approval.getWorkflow().getCreatedAt());
            workflowCopy.setUpdatedAt(approval.getWorkflow().getUpdatedAt());
            workflowCopy.setCreatedBy(approval.getWorkflow().getCreatedBy());
            workflowCopy.setUpdatedBy(approval.getWorkflow().getUpdatedBy());
            workflowCopy.setStatus(approval.getWorkflow().getStatus());
            // Xóa các quan hệ con để tránh vòng lặp
            workflowCopy.setWorkflowApprovalGroups(null);
            workflowCopy.setWorkflowSampleReports(null);
            approvalDTO.setWorkflow(workflowCopy);
        } else {
            approvalDTO.setWorkflow(null);
        }
        return approvalDTO;
    }

    public Approval mapToEntity(final ApprovalDTO approvalDTO, final Approval approval) {
        approval.setEntityType(approvalDTO.getEntityType());
        approval.setEntityId(approvalDTO.getEntityId());
        approval.setEntityId(approvalDTO.getEntityId());
        approval.setUserApproval(approvalDTO.getUserApproval());
        approval.setWorkflow(approvalDTO.getWorkflow());
        approval.setStatus(approvalDTO.getStatus());
        approval.setSignedAt(approvalDTO.getSignedAt());
        approval.setNote(approvalDTO.getNote());
        approval.setCreatedAt(approvalDTO.getCreatedAt());
        approval.setUpdatedAt(approvalDTO.getUpdatedAt());
        approval.setCreatedBy(approvalDTO.getCreatedBy());
        approval.setUpdatedBy(approvalDTO.getUpdatedBy());
        return approval;
    }

}
