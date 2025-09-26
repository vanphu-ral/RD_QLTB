package io.rd.qltb.repos;

import io.rd.qltb.domain.Approval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    public List<Approval> findByEntityTypeAndEntityId(String entityType, Long entityId);
}
