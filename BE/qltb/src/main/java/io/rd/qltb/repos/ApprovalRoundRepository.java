package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRoundRepository extends JpaRepository<ApprovalRound,Long> {
    @Query(value = "SELECT * FROM approval_round  WHERE entity_type = ?1 AND entity_id = ?2 ;",nativeQuery = true)
    ApprovalRound findPreviousRoundIdByEntityTypeAndEntityId(String entityType, Long entityId);
}
