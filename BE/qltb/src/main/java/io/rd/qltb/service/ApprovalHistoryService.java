package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalHistory;
import io.rd.qltb.model.ApprovalHistoryDTO;
import io.rd.qltb.repos.ApprovalHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ApprovalHistoryService {
@Autowired
private ApprovalHistoryRepository approvalHistoryRepository;
    private Function<? super ApprovalHistory,?> toDTO;

    public static ApprovalHistoryDTO toDTO(ApprovalHistory entity) {
        ApprovalHistoryDTO dto = new ApprovalHistoryDTO();
        dto.setId(entity.getId());
        dto.setEntityType(entity.getEntityType());
        dto.setEntityId(entity.getEntityId());
        dto.setWorkflowId(entity.getWorkflowId());
        dto.setOldApprovalData(entity.getOldApprovalData());
        dto.setOldRoundStatus(entity.getOldRoundStatus());
        return dto;
    }

    public static ApprovalHistory toEntity(ApprovalHistoryDTO dto) {
        ApprovalHistory entity = new ApprovalHistory();
        entity.setEntityType(dto.getEntityType());
        entity.setEntityId(dto.getEntityId());
        entity.setWorkflowId(dto.getWorkflowId());
        entity.setOldApprovalData(dto.getOldApprovalData());
        entity.setOldRoundStatus(dto.getOldRoundStatus());
        return entity;
    }
    // Thêm mới
    public ApprovalHistoryDTO create(ApprovalHistoryDTO dto) {
        ApprovalHistory entity = toEntity(dto);
        ApprovalHistory saved = approvalHistoryRepository.save(entity);
        return toDTO(saved);
    }

    // Sửa
    public ApprovalHistoryDTO update(Long id, ApprovalHistoryDTO dto) {
        ApprovalHistory entity = approvalHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ApprovalHistory not found"));

        entity.setEntityType(dto.getEntityType());
        entity.setEntityId(dto.getEntityId());
        entity.setWorkflowId(dto.getWorkflowId());
        entity.setOldApprovalData(dto.getOldApprovalData());
        entity.setOldRoundStatus(dto.getOldRoundStatus());

        ApprovalHistory updated = approvalHistoryRepository.save(entity);
        return toDTO(updated);
    }

    // Xoá
    public void delete(Long id) {
        approvalHistoryRepository.deleteById(id);
    }

    // Lấy tất cả
//    public List<ApprovalHistoryDTO> getAll() {
//        return approvalHistoryRepository.findAll()
//                .stream()
//                .map(toDTO)
//                .collect(Collectors.toList());
//    }

    // Lấy theo ID
    public ApprovalHistoryDTO getById(Long id) {
        ApprovalHistory entity = approvalHistoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ApprovalHistory not found"));
        return toDTO(entity);
    }
}
