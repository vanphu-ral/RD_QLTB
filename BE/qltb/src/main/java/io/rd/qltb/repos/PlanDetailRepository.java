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
    List<PlanDetail> findAllByDeviceIdAndStatusIn(Long id, List<Integer> statusList);

    @Modifying
    @Transactional
    @Query("DELETE FROM PlanDetail d WHERE d.plan.id = :planId")
    void deleteAllByPlanId(@Param("planId") Long planId);

    @Query("SELECT pd FROM PlanDetail pd " +
           "JOIN FETCH pd.plan p " +
           "JOIN FETCH p.planType pt " +
           "LEFT JOIN FETCH pd.device d " +
           "LEFT JOIN FETCH d.branch " +
           "LEFT JOIN FETCH d.team " +
           "LEFT JOIN FETCH d.line " +
           "LEFT JOIN FETCH pd.deviceGroup " +
           "LEFT JOIN FETCH pd.sampleReport " +
           "WHERE pt.code = 'DAILYCHECK' " +
           "AND p.status != 10 " +
           "AND pd.status != 10")
    List<PlanDetail> findAllDailyCheckPlanDetails();
}
