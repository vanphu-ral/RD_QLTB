package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanTargetResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlanTargetResultRepository extends JpaRepository<PlanTargetResult, Long> {

    PlanTargetResult findFirstByPlanTargetDeviceId(Long id);

    List<PlanTargetResult> findByPlanTargetDevice_Id(Long planTargetId);

}
