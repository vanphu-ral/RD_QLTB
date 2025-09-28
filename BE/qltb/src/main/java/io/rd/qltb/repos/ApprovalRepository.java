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
            value = "SELECT * FROM approvals WHERE user_approval_id IN (:userIds)",
            nativeQuery = true
    )
    List<Approval> findApprovalsByUserIds(@Param("userIds") List<Long> userIds);
}
