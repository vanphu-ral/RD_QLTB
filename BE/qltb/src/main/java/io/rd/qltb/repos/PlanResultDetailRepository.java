package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PlanResultDetailRepository extends JpaRepository<PlanResultDetail, Long> {

    PlanResultDetail findFirstByPlanResultId(Long id);
    List<PlanResultDetail> findByPlanResultId(Long id);
    @Query(value = "SELECT a.* FROM  plan_result_details a" +
            " inner join plan_results b on b.id = a.plan_result_id" +
            " inner join plan_details c on c.id = b.plan_detail_id  WHERE c.id = ?1 ;",nativeQuery = true)
    public List<PlanResultDetail> getByPlanDetailId(Long planDetailId);

    @Query(value = "SELECT count(*) FROM qltb.plan_result_details a\n" +
            "inner join qltb.plan_results b on b.id = a.plan_result_id\n" +
            "inner join qltb.plan_details c on c.id = b.plan_detail_id where c.device_id = ?1 and c.plan_id = ?2 ;",nativeQuery = true)
    public Integer countByDeviceIdAndPlanId(Long deviceId, Long planId);
}
