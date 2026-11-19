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

    @Query("SELECT pd FROM PlanDetail pd " +
           "JOIN FETCH pd.plan p " +
           "JOIN FETCH p.planType pt " +
           "LEFT JOIN FETCH p.branch pb " +
           "LEFT JOIN FETCH pd.device d " +
           "LEFT JOIN FETCH d.branch b " +
           "LEFT JOIN FETCH d.team " +
           "LEFT JOIN FETCH d.line " +
           "LEFT JOIN FETCH pd.deviceGroup " +
           "LEFT JOIN FETCH pd.sampleReport " +
           "WHERE pd.status = 5 " +
           "AND pt.code IN ('MAINTENANCE', 'REPAIR') " +
           "AND (:branch IS NULL OR :branch = '' OR LOWER(b.name) = LOWER(:branch) OR LOWER(b.code) = LOWER(:branch) OR (pb IS NOT NULL AND (LOWER(pb.name) = LOWER(:branch) OR LOWER(pb.code) = LOWER(:branch)))) " +
           "AND (p.status IS NULL OR p.status != 10) " +
           "AND NOT EXISTS (SELECT 1 FROM Acceptance a WHERE a.planDetailId = pd.id AND (a.status IS NULL OR a.status != 10)) " +
           "ORDER BY pd.updatedAt DESC, pd.id DESC")
    List<PlanDetail> findUnrecordedCompletedPlanDetails(@Param("branch") String branch);

    @Query("SELECT DISTINCT pd FROM PlanDetail pd " +
           "JOIN FETCH pd.plan p " +
           "JOIN FETCH p.planType pt " +
           "LEFT JOIN FETCH pd.device d " +
           "LEFT JOIN FETCH d.line " +
           "LEFT JOIN FETCH pd.planResults pr " +
           "WHERE d.id = :deviceId " +
           "AND pt.code = 'MAINTENANCE' " +
           "AND pd.status IN (4, 5) " +
           "AND (p.status IS NULL OR p.status != 10) " +
           "AND (:year IS NULL OR YEAR(pd.updatedAt) = :year OR YEAR(p.fromDate) = :year OR YEAR(pd.estimatedTime) = :year) " +
           "ORDER BY pd.updatedAt ASC")
    List<PlanDetail> findMaintenanceDetailsByDeviceAndYear(
           @Param("deviceId") Long deviceId,
           @Param("year") Integer year);

    @Query("SELECT DISTINCT pd FROM PlanDetail pd " +
           "JOIN FETCH pd.plan p " +
           "JOIN FETCH p.planType pt " +
           "LEFT JOIN FETCH pd.device d " +
           "LEFT JOIN FETCH d.line " +
           "LEFT JOIN FETCH d.branch b " +
           "LEFT JOIN FETCH b.factory f " +
           "LEFT JOIN FETCH pd.planResults pr " +
           "WHERE (:deviceId IS NULL OR d.id = :deviceId) " +
           "AND (:branchId IS NULL OR b.id = :branchId) " +
           "AND (:factoryId IS NULL OR f.id = :factoryId) " +
           "AND pt.code = 'MAINTENANCE' " +
           "AND pd.status IN (4, 5) " +
           "AND (p.status IS NULL OR p.status != 10) " +
           "AND (:year IS NULL OR YEAR(pd.updatedAt) = :year OR YEAR(p.fromDate) = :year OR YEAR(pd.estimatedTime) = :year) " +
           "ORDER BY pd.updatedAt ASC")
    List<PlanDetail> findMaintenanceDetailsByFilter(
           @Param("deviceId") Long deviceId,
           @Param("branchId") Long branchId,
           @Param("factoryId") Long factoryId,
           @Param("year") Integer year);

    @Query(value = "SELECT " +
           "    d.id AS device_id, " +
           "    d.code AS device_code, " +
           "    d.name AS device_name, " +
           "    d.qr_code AS qr_code, " +
           "    b.name AS branch_name, " +
           "    t.name AS team_name, " +
           "    l.name AS line_name, " +
           "    p.id AS plan_id, " +
           "    p.code AS plan_code, " +
           "    p.name AS plan_name, " +
           "    pt.code AS plan_type_code, " +
           "    pd.id AS plan_detail_id, " +
           "    CASE WHEN pr_today.id IS NOT NULL THEN 1 ELSE 0 END AS today_checked, " +
           "    pr_today.status AS today_status, " +
           "    pr_today.updated_at AS last_checked_time " +
           "FROM plan_details pd " +
           "JOIN plans p ON pd.plan_id = p.id " +
           "JOIN plan_types pt ON p.plan_type_id = pt.id " +
           "JOIN devices d ON pd.device_id = d.id " +
           "LEFT JOIN branches b ON d.branch_id = b.id " +
           "LEFT JOIN teams t ON d.team_id = t.id " +
           "LEFT JOIN `lines` l ON d.line_id = l.id " +
           "LEFT JOIN ( " +
           "    SELECT " +
           "        pr.plan_detail_id, " +
           "        MAX(pr.id) AS id, " +
           "        MAX(pr.status) AS status, " +
           "        MAX(COALESCE(pr.updated_at, pr.created_at)) AS updated_at " +
           "    FROM plan_results pr " +
           "    WHERE DATE(pr.date_test) = :today OR DATE(pr.updated_at) = :today " +
           "    GROUP BY pr.plan_detail_id " +
           ") pr_today ON pr_today.plan_detail_id = pd.id " +
           "WHERE (p.status IS NULL OR p.status != 10) AND (pd.status IS NULL OR pd.status != 10) " +
           "  AND pt.code IN ('DAILYCHECK', 'MAINTENANCE') " +
           "  AND (:planType IS NULL OR :planType = '' OR pt.code = :planType) " +
           "  AND ( " +
           "      FIND_IN_SET(:username, pd.manager) > 0 " +
           "      OR LOWER(pd.manager) LIKE CONCAT('%', LOWER(:username), '%') " +
           "      OR FIND_IN_SET(:username, p.user_performer) > 0 " +
           "      OR LOWER(p.user_performer) LIKE CONCAT('%', LOWER(:username), '%') " +
           "  ) " +
           "  AND ( " +
           "      (CURRENT_DATE BETWEEN DATE(p.from_date) AND DATE(p.to_date)) " +
           "      OR p.from_date IS NULL " +
           "      OR p.status IN (1, 4) " +
           "  ) " +
           "ORDER BY today_checked ASC, d.name ASC", nativeQuery = true)
    List<Object[]> findMyAssignedDevices(
           @Param("username") String username,
           @Param("planType") String planType,
           @Param("today") String today);
}
