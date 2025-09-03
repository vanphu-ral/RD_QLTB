package io.qltb.qltb.repos;

import io.qltb.qltb.domain.PlanTarget;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanTargetRepository extends JpaRepository<PlanTarget, Long> {

    PlanTarget findFirstByBranchId(Long id);

}
