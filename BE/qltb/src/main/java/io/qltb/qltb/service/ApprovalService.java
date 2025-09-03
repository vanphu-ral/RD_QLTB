package io.qltb.qltb.service;

import io.qltb.qltb.domain.Approval;
import io.qltb.qltb.model.ApprovalDTO;
import io.qltb.qltb.repos.ApprovalRepository;
import io.qltb.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;

    public ApprovalService(final ApprovalRepository approvalRepository) {
        this.approvalRepository = approvalRepository;
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

    private ApprovalDTO mapToDTO(final Approval approval, final ApprovalDTO approvalDTO) {
        approvalDTO.setId(approval.getId());
        approvalDTO.setEntityType(approval.getEntityType());
        approvalDTO.setEntityId(approval.getEntityId());
        approvalDTO.setApproverId(approval.getApproverId());
        approvalDTO.setStatus(approval.getStatus());
        approvalDTO.setSignedAt(approval.getSignedAt());
        approvalDTO.setNote(approval.getNote());
        approvalDTO.setCreatedAt(approval.getCreatedAt());
        approvalDTO.setUpdatedAt(approval.getUpdatedAt());
        approvalDTO.setCreatedBy(approval.getCreatedBy());
        approvalDTO.setUpdatedBy(approval.getUpdatedBy());
        return approvalDTO;
    }

    private Approval mapToEntity(final ApprovalDTO approvalDTO, final Approval approval) {
        approval.setEntityType(approvalDTO.getEntityType());
        approval.setEntityId(approvalDTO.getEntityId());
        approval.setApproverId(approvalDTO.getApproverId());
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
