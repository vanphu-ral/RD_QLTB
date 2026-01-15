package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.model.ApprovalDTO;
import io.rd.qltb.model.ApprovalRequestDTO;
import io.rd.qltb.model.ApprovalResponseDTO;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


@Service
public class ApprovalService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private final ApprovalRepository approvalRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final ApprovalGroupUserRepository approvalGroupUserRepository;
    private final ApprovalRoundRepository approvalRoundRepository;
    private final ApprovalGroupRepository approvalGroupRepository;
    private final GroupApprovalNameRepository groupApprovalNameRepository;

    public ApprovalService(JdbcTemplate jdbcTemplate, final ApprovalRepository approvalRepository, final ApprovalWorkflowRepository approvalWorkflowRepository, ApprovalGroupUserRepository approvalGroupUserRepository, ApprovalRoundRepository approvalRoundRepository, ApprovalGroupRepository approvalGroupRepository, GroupApprovalNameRepository groupApprovalNameRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.approvalRepository = approvalRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.approvalGroupUserRepository = approvalGroupUserRepository;
        this.approvalRoundRepository = approvalRoundRepository;
        this.approvalGroupRepository = approvalGroupRepository;
        this.groupApprovalNameRepository = groupApprovalNameRepository;
    }
    public List<ApprovalResponseDTO> getAllFromTable() {
        List<Approval> approvals = approvalRepository.findAll();
        return approvals.stream().map(approval -> {
            ApprovalResponseDTO responseDTO = new ApprovalResponseDTO();
            responseDTO.setApproval(mapToDTO(approval, new ApprovalDTO()));
            String tableName =  approval.getEntityType() ;
            String sql = "SELECT * FROM " + tableName + " WHERE id = ? ";
            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, approval.getEntityId());
            if (!data.isEmpty()) {
                responseDTO.setData(data.get(0));
            } else {
                responseDTO.setData(null);
            }
            return responseDTO;
        }).toList();
    }
    public List<ApprovalResponseDTO> getAllFromTableByUserName(String userName) {
        // 1. Tìm danh sách GroupUser
        List<ApprovalGroupUser> approvalGroupUsers = approvalGroupUserRepository.findAllByUsername(userName);
        if (approvalGroupUsers.isEmpty()) {
            throw new NotFoundException("User not found in any approval group");
        }

        List<Long> userIds = approvalGroupUsers.stream().map(ApprovalGroupUser::getId).toList();

        // 2. Truy vấn danh sách approvals (Đảm bảo biến này được định nghĩa tại đây)
        // ĐẶT BREAKPOINT TẠI ĐÂY: Kiểm tra list approvals có dữ liệu không
        List<Approval> approvals = approvalRepository.findApprovalsByUserIds(userIds, userName);

        if (approvals == null || approvals.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Xử lý map dữ liệu
        return approvals.stream()
                .map(this::enrichApprovalData) // Gọi hàm phụ bên dưới
                .sorted(Comparator.comparing(
                        (ApprovalResponseDTO a) -> a.getApproval().getCheckStatus(),
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }

    /**
     * Hàm tách riêng để dễ Debug từng dòng
     */
    private ApprovalResponseDTO enrichApprovalData(Approval approval) {
        ApprovalResponseDTO responseDTO = new ApprovalResponseDTO();

        // Bước A: Map DTO cơ bản
        ApprovalDTO approvalDTO = mapToDTO(approval, new ApprovalDTO());
        approvalDTO.setCheckStatus(1); // Mặc định 1
        responseDTO.setApproval(approvalDTO);

        // Bước B: Lấy dữ liệu từ bảng nghiệp vụ (Sửa lỗi hàng 7-10 bị trống)
        try {
            String entityType = approval.getEntityType();
            Long entityId = approval.getEntityId();

            if (entityType != null && entityId != null) {
                String sql = "SELECT * FROM " + entityType + " WHERE id = ?";
                if (entityType.equals("plan_details")){
                  sql = "SELECT pd.*, d.code AS code, d.name AS name " +
                          "FROM plan_details pd " +
                          "JOIN devices d ON pd.device_id = d.id " +
                          "WHERE pd.id = ? ";
                }
                // ĐẶT BREAKPOINT TẠI ĐÂY: Kiểm tra kết quả SQL trả về cho từng dòng
                List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql, entityId);

                if (!dataList.isEmpty()) {
                    responseDTO.setData(dataList.get(0));
                } else {
                    // Nếu hàng 7-10 trống, code sẽ chạy vào đây
                    Map<String, Object> fallback = new HashMap<>();
                    fallback.put("error", "Dữ liệu gốc đã bị xóa hoặc sai ID: " + entityId);
                    responseDTO.setData(fallback);
                }
            }
        } catch (Exception e) {
            responseDTO.setData(Collections.singletonMap("error", "Lỗi SQL: " + e.getMessage()));
        }

        // Bước C: Tính toán CheckStatus
        if (approval.getGroup() != null && approval.getWorkflow() != null) {
            int checkStatus = 1;
            int level = approval.getGroup().getLevel();

            if (level > 0) {
                ApprovalGroup prevGroup = approvalGroupRepository.findByWorkflowIdAndLevel(
                        approval.getWorkflow().getId(), level - 1);

                if (prevGroup != null) {
                    Integer pendingCount = approvalRepository.countPendingByGroupIdAndWorkflowIdAndEntityId(
                            prevGroup.getId(), approval.getWorkflow().getId(), approval.getEntityId());

                    checkStatus = (pendingCount != null && pendingCount > 0) ? 0 : 1;
                }
            }
            approvalDTO.setCheckStatus(checkStatus);
        }

        return responseDTO;
    }
    public List<ApprovalDTO> findApprovalsByEntityIdAndEntityType(String entity,String entityType){
        List<ApprovalDTO> approvalDTOS = approvalRepository.findApprovalsByEntityIdAndEntityType(entity,entityType).stream().map(
                approval -> mapToDTO(approval,new ApprovalDTO())).toList();
        for (ApprovalDTO approval: approvalDTOS){
        // Thêm tên nhóm phê duyệt vào ApprovalDTO
            // Thêm tên nhóm phê duyệt vào ApprovalDTO
            ApprovalGroup group = approvalGroupRepository.findById(approval.getGroup().getId()).orElseThrow(()-> new NotFoundException("ApprovalGroup not found"));
            approval.getGroup().setGroupApprovalName(groupApprovalNameRepository.findById(group.getGroupApprovalName().getId()).orElseThrow(()-> new NotFoundException("GroupApprovalName not found")));
            System.out.println("Group Approval Name: " + approval.getGroup().getGroupApprovalName().getName());
            approval.getGroup().getGroupApprovalName().setApprovalGroups(null); // tránh vòng lặp
        }
        return approvalDTOS;
    }
    public List<ApprovalDTO> findAll() {
        final List<Approval> approvals = approvalRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
        if(approvalRepository.getNumberOfApproveEntity(approval.getEntityId(),approval.getEntityType()) >0){
        String query =" update " + approval.getEntityType() + " set status = 3  WHERE id = " + approval.getEntityId();
        jdbcTemplate.update(query);
        }
        approvalRepository.save(approval);
//        // Cập nhật trạng thái của ApprovalRound dựa trên trạng thái của Approval
//        Integer total = approvalRepository.countByRoundIdAndEntityId(approval.getRound().getId(),approval.getEntityId());
//        if(approval.getStatus() == 6){
//            ApprovalRound approvalRound = approvalRoundRepository.findById(approval.getRound().getId()).orElseThrow(()-> new NotFoundException("ApprovalRound not found"));
//            approvalRound.setStatus(6);
//                approvalRoundRepository.save(approvalRound);
//            String tableName = approval.getEntityType();
//            String sql = "update " + tableName + " set status = 6 WHERE id = ?";
//             jdbcTemplate.update(sql, approval.getEntityId());
//        }else if(approvalRepository.countByRoundIdAndEntityIdAndStatus(approval.getRound().getId(),approval.getEntityId(),6) >0){
//            ApprovalRound approvalRound = approvalRoundRepository.findById(approval.getRound().getId()).orElseThrow(()-> new NotFoundException("ApprovalRound not found"));
//            approvalRound.setStatus(6);
//            approvalRoundRepository.save(approvalRound);
//            String tableName = approval.getEntityType();
//            String sql = "update " + tableName + " set status = 6 WHERE id = ?";
//            jdbcTemplate.update(sql, approval.getEntityId());
//        } else if (approvalRepository.countByRoundIdAndEntityIdAndStatus(approval.getRound().getId(),approval.getEntityId(),6) ==0
//        && approvalRepository.countByRoundIdAndEntityIdAndStatus(approval.getRound().getId(),approval.getEntityId(),3) == total) {
//            ApprovalRound approvalRound = approvalRoundRepository.findById(approval.getRound().getId()).orElseThrow(()-> new NotFoundException("ApprovalRound not found"));
//            approvalRound.setStatus(3);
//            approvalRoundRepository.save(approvalRound);
//            String tableName = approval.getEntityType();
//            String sql = "update " + tableName + " set status = 3 WHERE id = ?";
//            jdbcTemplate.update(sql, approval.getEntityId());
//        } else if (approvalRepository.countByRoundIdAndEntityIdAndStatus(approval.getRound().getId(),approval.getEntityId(),6) ==0
//                && approvalRepository.countByRoundIdAndEntityIdAndStatus(approval.getRound().getId(),approval.getEntityId(),3) < total) {
//            ApprovalRound approvalRound = approvalRoundRepository.findById(approval.getRound().getId()).orElseThrow(()-> new NotFoundException("ApprovalRound not found"));
//            approvalRound.setStatus(2);
//            approvalRoundRepository.save(approvalRound);
//            String tableName = approval.getEntityType();
//            String sql = "update " + tableName + " set status = 2 WHERE id = ?";
//            jdbcTemplate.update(sql, approval.getEntityId());
//        }
    }

    public void delete(final Long id) {
        final Approval approval = approvalRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        approvalRepository.delete(approval);
    }

    public void createScriptApproval(ApprovalRequestDTO approvalRequestDTO, String entityType, String userName){
        ApprovalWorkflow approvalWorkflow = approvalWorkflowRepository.findById(approvalRequestDTO.getWorkflowId()).orElseThrow(()-> new NotFoundException("approvalWorkflow not found"));
//        ApprovalRound newRound = new ApprovalRound();
//        if(approvalRequestDTO.getPreviousEntityId() != null){ //nếu có previousEntityId thì tạo vòng tiếp theo
//        ApprovalRound previousRound= approvalRoundRepository.findPreviousRoundIdByEntityTypeAndEntityId(entityType, approvalRequestDTO.getPreviousEntityId());
//            newRound.setEntityType(entityType);
//            newRound.setEntityId(approvalRequestDTO.getEntityId());
//            newRound.setRoundNumber(previousRound.getRoundNumber() + 1);
//            newRound.setPreviousRoundId(previousRound.getId());
//            newRound.setWorkflow(approvalWorkflow);
//            newRound.setStatus(1);
//            newRound.setCreatedAt(LocalDateTime.now());
//            newRound.setCreatedBy(userName);
//            approvalRoundRepository.save(newRound);
//        }else { //nếu không có previousEntityId thì tạo vòng đầu tiên
//            newRound.setEntityType(entityType);
//            newRound.setEntityId(approvalRequestDTO.getEntityId());
//            newRound.setRoundNumber(1);
//            newRound.setPreviousRoundId(null);
//            newRound.setWorkflow(approvalWorkflow);
//            newRound.setStatus(1);
//            newRound.setCreatedAt(LocalDateTime.now());
//            newRound.setCreatedBy(userName);
//            approvalRoundRepository.save(newRound);
//        }
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
//                approval.setRound(newRound);
                approval.setRound(null);
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
        approvalDTO.setUsername(approval.getUsername());
        // sao chep approvalRound có kiểm soát
//        if (approval.getRound() != null) {
//            ApprovalRound roundCopy = new ApprovalRound();
//            roundCopy.setId(approval.getRound().getId());
//            roundCopy.setEntityType(approval.getRound().getEntityType());
//            roundCopy.setEntityId(approval.getRound().getEntityId());
//            roundCopy.setRoundNumber(approval.getRound().getRoundNumber());
//            roundCopy.setPreviousRoundId(approval.getRound().getPreviousRoundId());
//            roundCopy.setStatus(approval.getRound().getStatus());
//            roundCopy.setCreatedAt(approval.getRound().getCreatedAt());
//            roundCopy.setCreatedBy(approval.getRound().getCreatedBy());
//            // Xóa các quan hệ con để tránh vòng lặp
//            roundCopy.setWorkflow(null);
//            approvalDTO.setRound(roundCopy);
//        } else {
//            approvalDTO.setRound(null);
//        }
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
            groupCopy.setLevel(approval.getGroup().getLevel());
            // Xóa các quan hệ con để tránh vòng lặp
            groupCopy.setWorkflow(null);
            groupCopy.setGroupApprovalGroupUsers(null);
            //  Xóa quan hệ với GroupApprovalName để tránh vòng lặp
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
        approval.setUsername(approvalDTO.getUsername());
//        approval.setRound(approvalDTO.getRound());
        return approval;
    }

}
