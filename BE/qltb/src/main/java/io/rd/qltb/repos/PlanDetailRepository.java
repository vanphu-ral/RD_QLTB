package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanDetailRepository extends JpaRepository<PlanDetail, Long> {

    PlanDetail findFirstByPlanId(Long id);

    PlanDetail findFirstByDeviceId(Integer id);

    PlanDetail findFirstByDeviceGroupId(Integer id);

}
