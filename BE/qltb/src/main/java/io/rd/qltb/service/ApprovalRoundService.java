package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalRound;
import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.model.ApprovalRoundDTO;
import io.rd.qltb.repos.ApprovalRoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalRoundService {
    @Autowired
    ApprovalRoundRepository approvalRoundRepository;

    public ApprovalRoundDTO mapToDTO(final ApprovalRound approvalRound, final ApprovalRoundDTO approvalRoundDTO) {
        approvalRoundDTO.setId(approvalRound.getId());
        approvalRoundDTO.setEntityType(approvalRound.getEntityType());
        approvalRoundDTO.setEntityId(approvalRound.getEntityId());
        approvalRoundDTO.setRoundNumber(approvalRound.getRoundNumber());
        approvalRoundDTO.setPreviousRoundId(approvalRound.getPreviousRoundId());
        approvalRoundDTO.setStatus(approvalRound.getStatus());
        approvalRoundDTO.setCreatedBy(approvalRound.getCreatedBy());

        // Chuyển đổi thời gian từ LocalDateTime sang String
        if (approvalRound.getCreatedAt() != null) {
            approvalRoundDTO.setCreatedAt(approvalRound.getCreatedAt());
        } else {
            approvalRoundDTO.setCreatedAt(null);
        }

        // Sao chép ApprovalWorkflow có kiểm soát
        if (approvalRound.getWorkflow() != null) {
            ApprovalWorkflow workflowCopy = new ApprovalWorkflow();
            workflowCopy.setId(approvalRound.getWorkflow().getId());
            workflowCopy.setCode(approvalRound.getWorkflow().getCode());
            workflowCopy.setName(approvalRound.getWorkflow().getName());
            workflowCopy.setDescription(approvalRound.getWorkflow().getDescription());
            workflowCopy.setCreatedAt(approvalRound.getWorkflow().getCreatedAt());
            workflowCopy.setUpdatedAt(approvalRound.getWorkflow().getUpdatedAt());
            workflowCopy.setCreatedBy(approvalRound.getWorkflow().getCreatedBy());
            workflowCopy.setUpdatedBy(approvalRound.getWorkflow().getUpdatedBy());
            workflowCopy.setStatus(approvalRound.getWorkflow().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            workflowCopy.setWorkflowApprovalGroups(null);
            workflowCopy.setWorkflowSampleReports(null);

            approvalRoundDTO.setWorkflow(workflowCopy);
        } else {
            approvalRoundDTO.setWorkflow(null);
        }

        return approvalRoundDTO;
    }
    public ApprovalRound mapToEntity(final ApprovalRoundDTO dto, final ApprovalRound entity) {
        entity.setEntityType(dto.getEntityType());
        entity.setEntityId(dto.getEntityId());
        entity.setRoundNumber(dto.getRoundNumber());
        entity.setPreviousRoundId(dto.getPreviousRoundId());
        entity.setStatus(dto.getStatus());
        entity.setCreatedBy(dto.getCreatedBy());

        // Chuyển đổi thời gian từ String sang LocalDateTime
        if (dto.getCreatedAt() != null) {
            entity.setCreatedAt(dto.getCreatedAt());
        } else {
            entity.setCreatedAt(null);
        }

        // Sao chép ApprovalWorkflow có kiểm soát
        if (dto.getWorkflow() != null) {
            ApprovalWorkflow workflowCopy = new ApprovalWorkflow();
            workflowCopy.setId(dto.getWorkflow().getId());
            workflowCopy.setCode(dto.getWorkflow().getCode());
            workflowCopy.setName(dto.getWorkflow().getName());
            workflowCopy.setDescription(dto.getWorkflow().getDescription());
            workflowCopy.setCreatedAt(dto.getWorkflow().getCreatedAt());
            workflowCopy.setUpdatedAt(dto.getWorkflow().getUpdatedAt());
            workflowCopy.setCreatedBy(dto.getWorkflow().getCreatedBy());
            workflowCopy.setUpdatedBy(dto.getWorkflow().getUpdatedBy());
            workflowCopy.setStatus(dto.getWorkflow().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            workflowCopy.setWorkflowApprovalGroups(null);
            workflowCopy.setWorkflowSampleReports(null);

            entity.setWorkflow(workflowCopy);
        } else {
            entity.setWorkflow(null);
        }

        return entity;
    }

}
