package io.qltb.qltb.repos;

import io.qltb.qltb.domain.PlanTargetResult;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanTargetResultRepository extends JpaRepository<PlanTargetResult, Long> {

    PlanTargetResult findFirstByPlanTargetDeviceId(Long id);

}
