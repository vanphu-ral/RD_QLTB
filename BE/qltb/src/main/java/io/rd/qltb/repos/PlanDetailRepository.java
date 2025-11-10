package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlanDetailRepository extends JpaRepository<PlanDetail, Long> {

    PlanDetail findFirstByPlanId(Long id);

    PlanDetail findFirstByDeviceId(Long id);

    PlanDetail findFirstByDeviceGroupId(Long id);
    List<PlanDetail> findAllByPlanId(Long id);
}
