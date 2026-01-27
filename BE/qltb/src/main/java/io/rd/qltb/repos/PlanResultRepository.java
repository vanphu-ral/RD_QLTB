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
    @Query(value = "SELECT pr.* FROM plan_results pr \n" +
            "INNER JOIN plan_details pd ON pr.plan_detail_id = pd.id \n" +
            "INNER JOIN devices d ON d.id = pd.device_id \n" +
            "INNER JOIN plans p ON pd.plan_id = p.id \n" +
            "WHERE d.id = 10 AND p.plan_type_id = 5 \n" +
            "ORDER BY pr.id DESC \n" +
            "LIMIT 1 ;" ,nativeQuery = true)
    PlanResult findLatestPlanResultByDeviceIdAndPlanTypeMaintenance(Long deviceId);
}
