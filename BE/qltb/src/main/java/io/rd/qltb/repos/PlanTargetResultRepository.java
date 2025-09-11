package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanTargetResult;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanTargetResultRepository extends JpaRepository<PlanTargetResult, Integer> {

    PlanTargetResult findFirstByPlanTargetDeviceId(Long id);

}
