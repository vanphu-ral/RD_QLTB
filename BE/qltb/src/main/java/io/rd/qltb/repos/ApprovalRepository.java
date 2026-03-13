package io.rd.qltb.repos;

import io.rd.qltb.domain.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    public List<Approval> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<Approval> findByEntityType(String entityType);
    // Hoặc dùng phương thức tự động của Spring Data
    @Query(
            value = "SELECT * FROM approvals WHERE user_approval_id IN (:userIds) or username = :username order by status ",
            nativeQuery = true
    )
    List<Approval> findApprovalsByUserIds(@Param("userIds") List<Long> userIds,
                                          @Param("username") String username);
    @Query(
            value = "SELECT * FROM approvals " +
                    "WHERE entity_id = :entity AND entity_type = :entityType " +
                    "AND sign_number = (SELECT MAX(sign_number) FROM approvals " +
                    "                   WHERE entity_id = :entity AND entity_type = :entityType)",
            nativeQuery = true
    )
    List<Approval> findApprovalsByEntityIdAndEntityType(@Param("entity") String entity,
                                                     @Param("entityType") String entityType);
    List<Approval> findByRoundId(Long roundId);
    @Query(value = "select count(status) from approvals where round_id = :roundId and entity_id = :entityId and status = :status ;", nativeQuery = true)
    Integer countByRoundIdAndEntityIdAndStatus(@Param("roundId") Long roundId, @Param("entityId") Long entityId, @Param("status") Integer status);
    @Query(value = "select count(*) from approvals where round_id = :roundId and entity_id = :entityId ;", nativeQuery = true)
    Integer countByRoundIdAndEntityId(@Param("roundId") Long roundId, @Param("entityId") Long entityId);
    @Query(value="select count(*) from approvals a where a.status != 3 and group_id = ?1 and workflow_id = ?2  ;", nativeQuery = true)
    Integer countPendingByGroupIdAndWorkflowId(Long groupId, Long workflowId);
    @Query(value = "select count(*) from approvals where entity_id = :entityId and entity_type = :entityType and status = 3 ;", nativeQuery = true)
    public Integer  getNumberOfApproveEntity(@Param("entityId") Long entityId,
                                            @Param("entityType") String entityType);
    // Truy vấn đếm số lượng bản ghi ở level trước đó mà CHƯA HOÀN THÀNH
    // Giả sử status = 0 là PENDING (chưa duyệt)
    @Query("SELECT COUNT(a) FROM Approval a " +
            "WHERE a.group.id = :groupId " +
            "AND a.workflow.id = :workflowId " +
            "AND a.entityId = :entityId " +
            "AND a.status = 1") // Thay đổi status = 0, 1, hoặc 'PENDING' tùy theo DB của bạn
    Integer countPendingByGroupIdAndWorkflowIdAndEntityId(
            @Param("groupId") Long groupId,
            @Param("workflowId") Long workflowId,
            @Param("entityId") Long entityId
    );
    @Query(value ="select max(sign_number) from approvals where entity_id = ?1 and entity_type = ?2 ;", nativeQuery = true)
    Integer getMaxIndexByEntityIdAndEntityType( Long entityId,
                                                String entityType);
}
