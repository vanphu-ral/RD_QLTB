package io.rd.qltb.repos;

import io.rd.qltb.domain.Approval;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
