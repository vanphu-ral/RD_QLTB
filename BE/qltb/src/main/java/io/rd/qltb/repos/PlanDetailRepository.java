package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface PlanDetailRepository extends JpaRepository<PlanDetail, Long> {

    PlanDetail findFirstByPlanId(Long id);

    PlanDetail findFirstByDeviceId(Long id);

    PlanDetail findFirstByDeviceGroupId(Long id);
    List<PlanDetail> findAllByPlanId(Long id);
    List<PlanDetail> findAllByDeviceId(Long id);
    List<PlanDetail> findAllByDeviceIdAndStatus(Long id,Integer status);

    @Modifying
    @Transactional
    @Query("DELETE FROM PlanDetail d WHERE d.plan.id = :planId")
    void deleteAllByPlanId(@Param("planId") Long planId);
}
