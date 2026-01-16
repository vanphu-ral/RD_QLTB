package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanResult;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PlanResultRepository extends JpaRepository<PlanResult, Long> {
    List<PlanResult> findByPlanDetailId(Long planDetailId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM plan_results pr WHERE pr.plan_detail_id = ?1 ",nativeQuery = true)
    void deleteAllByPlanDetailId(Long planDetailId);
    @Query(value = "SELECT * FROM plan_results pr WHERE pr.plan_detail_id = ?1 and date_test like ?2 ;",nativeQuery = true)
    List<PlanResult> findByPlanDetailIdAndDateTestLike(Long planDetailId, String dateTestPattern);
}
