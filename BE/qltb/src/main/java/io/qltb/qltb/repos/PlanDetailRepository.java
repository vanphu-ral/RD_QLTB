package io.qltb.qltb.repos;

import io.qltb.qltb.domain.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanDetailRepository extends JpaRepository<PlanDetail, Long> {

    PlanDetail findFirstByPlanId(Long id);

    PlanDetail findFirstByDeviceId(Long id);

    PlanDetail findFirstByDeviceGroupId(Long id);

}
