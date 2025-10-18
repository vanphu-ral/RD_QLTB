package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRoundRepository extends JpaRepository<ApprovalRound,Long> {
}
