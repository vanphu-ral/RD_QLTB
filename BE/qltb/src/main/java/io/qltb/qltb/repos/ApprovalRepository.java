package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Approval;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
