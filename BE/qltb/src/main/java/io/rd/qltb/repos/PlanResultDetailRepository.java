package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanResultDetail;
import io.rd.qltb.model.response.Report2Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface PlanResultDetailRepository extends JpaRepository<PlanResultDetail, Long> {

    PlanResultDetail findFirstByPlanResultId(Long id);
    List<PlanResultDetail> findByPlanResultId(Long id);
    @Query(value = "SELECT a.* FROM  plan_result_details a" +
            " inner join plan_results b on b.id = a.plan_result_id" +
            " inner join plan_details c on c.id = b.plan_detail_id  WHERE c.id = ?1 ;",nativeQuery = true)
    public List<PlanResultDetail> getByPlanDetailId(Long planDetailId);
    @Query(value = "SELECT count(*) FROM  plan_result_details a" +
            " inner join plan_results b on b.id = a.plan_result_id" +
            " inner join plan_details c on c.id = b.plan_detail_id  WHERE c.id = ?1 and a.created_by = ?2 ;",nativeQuery = true)
    public Integer countByPlanDetailId(Long planDetailId,String createBy);
    @Query(value = "SELECT count(*) FROM device_management.plan_result_details a\n" +
            "inner join device_management.plan_results b on b.id = a.plan_result_id\n" +
            "inner join device_management.plan_details c on c.id = b.plan_detail_id where c.device_id = ?1 and c.plan_id = ?2 ;",nativeQuery = true)
    public Integer countByDeviceIdAndPlanId(Long deviceId, Long planId);
    @Query(value = "SELECT count(*) FROM device_management.plan_result_details a\n" +
            "inner join device_management.plan_results b on b.id = a.plan_result_id\n" +
            "inner join device_management.plan_details c on c.id = b.plan_detail_id where c.id = ?1 ;",nativeQuery = true)
    public Integer getCountByPlanDetailId( Long planDetailId);
    @Query(
            value = "SELECT " +
                    "b.name as branchName, " +
                    "d.code as deviceCode, " +
                    "d.name as deviceName, " +
                    "p.code as planCode, " +
                    "DATE_FORMAT(pr.date_test, '%Y-%m-%d') as dateTest, " +
                    "prd.critical_name as criticalName, " +
                    "prd.committee as committee, " +
                    "prd.result as result " +
                    "FROM device_management.plan_result_details prd " +
                    "INNER JOIN device_management.plan_results pr ON pr.id = prd.plan_result_id " +
                    "INNER JOIN device_management.plan_details pd ON pd.id = pr.plan_detail_id " +
                    "INNER JOIN device_management.devices d ON pd.device_id = d.id " +
                    "INNER JOIN device_management.plans p ON p.id = pd.plan_id " +
                    "INNER JOIN device_management.branches b ON b.id = p.branch_id " +
                    "INNER JOIN device_management.plan_types pt ON pt.id = p.plan_type_id " +
                    "WHERE pt.code = 'MAINTENANCE' " +
                    "AND  p.branch_id IN (:branchIds) " +
                    "AND DATE_FORMAT(pr.date_test, '%Y-%m-%d') BETWEEN :startDate AND :endDate ",
            countQuery = "SELECT COUNT(*) " +
                    "FROM device_management.plan_result_details prd " +
                    "INNER JOIN device_management.plan_results pr ON pr.id = prd.plan_result_id " +
                    "INNER JOIN device_management.plan_details pd ON pd.id = pr.plan_detail_id " +
                    "INNER JOIN device_management.devices d ON pd.device_id = d.id " +
                    "INNER JOIN device_management.plans p ON p.id = pd.plan_id " +
                    "INNER JOIN device_management.branches b ON b.id = p.branch_id " +
                    "INNER JOIN device_management.plan_types pt ON pt.id = p.plan_type_id " +
                    "WHERE pt.code = 'MAINTENANCE' " +
                    "AND  p.branch_id IN (:branchIds) " +
                    "AND DATE_FORMAT(pr.date_test, '%Y-%m-%d') BETWEEN :startDate AND :endDate ",
            nativeQuery = true
    )
    Page<Report2Response> getMaintenanceReportByBranchAndDateRange(
            @Param("branchIds") List<Long> branchIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            Pageable pageable
    );

    @Query(value = "SELECT a.* FROM plan_result_details a" +
            " INNER JOIN plan_results b ON b.id = a.plan_result_id" +
            " INNER JOIN plan_details c ON c.id = b.plan_detail_id" +
            " WHERE c.id = ?1" +
            " AND MONTH(b.date_test) = ?2" +
            " AND YEAR(b.date_test) = ?3", nativeQuery = true)
    List<PlanResultDetail> getByPlanDetailIdAndMonth(Long planDetailId, int month, int year);

}
