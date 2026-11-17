package io.rd.qltb.repos;

import io.rd.qltb.domain.Acceptance;
import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.PlanDetail;
import io.rd.qltb.domain.ReportDeviceIncident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Query native/JPQL phục vụ dashboard — tach rieng de gom filter branch/team/group.
 */
public interface DashboardRepository extends JpaRepository<Device, Long> {

    @Query("SELECT COUNT(d) FROM Device d " +
           "WHERE (d.status IS NULL OR d.status <> :deleted) " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId)")
    long countAllDevices(
            @Param("deleted") Integer deleted,
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId);

    @Query("SELECT COUNT(d) FROM Device d " +
           "WHERE d.state = 1 AND (d.status IS NULL OR d.status <> :deleted) " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId)")
    long countWorkingDevices(
            @Param("deleted") Integer deleted,
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId);

    @Query(value = "SELECT COUNT(DISTINCT pd.device_id) FROM plan_details pd " +
            "INNER JOIN plans p ON pd.plan_id = p.id " +
            "INNER JOIN plan_types pt ON p.plan_type_id = pt.id " +
            "INNER JOIN devices d ON pd.device_id = d.id " +
            "WHERE pt.code = 'MAINTENANCE' AND p.status = 4 " +
            "AND (pd.status IS NULL OR pd.status <> 10) " +
            "AND (d.status IS NULL OR d.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId) " +
            "AND pd.estimated_time IS NOT NULL " +
            "AND YEAR(pd.estimated_time) = :year AND MONTH(pd.estimated_time) = :month",
            nativeQuery = true)
    long countMonthlyMaintenancePlanDevices(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("year") int year,
            @Param("month") int month);

    @Query(value = "SELECT COUNT(DISTINCT pd.device_id) FROM plan_details pd " +
            "INNER JOIN plans p ON pd.plan_id = p.id " +
            "INNER JOIN plan_types pt ON p.plan_type_id = pt.id " +
            "INNER JOIN devices d ON pd.device_id = d.id " +
            "WHERE pt.code = 'MAINTENANCE' AND p.status = 4 " +
            "AND (pd.status IS NULL OR pd.status <> 10) " +
            "AND (d.status IS NULL OR d.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId) " +
            "AND (:year IS NULL OR :year = 0 OR YEAR(COALESCE(p.from_date, pd.updated_at)) = :year)",
            nativeQuery = true)
    long countYearlyMaintenancePlanDevices(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("year") int year);

    @Query(value = "SELECT COUNT(DISTINCT pd.device_id) FROM plan_details pd " +
            "INNER JOIN plans p ON pd.plan_id = p.id " +
            "INNER JOIN plan_types pt ON p.plan_type_id = pt.id " +
            "INNER JOIN devices d ON pd.device_id = d.id " +
            "WHERE pt.code = 'MAINTENANCE' AND pd.status = 5 " +
            "AND (p.status IS NULL OR p.status <> 10) " +
            "AND (d.status IS NULL OR d.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId)",
            nativeQuery = true)
    long countMaintainedDevices(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId);

    @Query(value = "SELECT COUNT(pr.id) FROM plan_results pr " +
            "INNER JOIN plan_details pd ON pr.plan_detail_id = pd.id " +
            "INNER JOIN plans p ON pd.plan_id = p.id " +
            "INNER JOIN plan_types pt ON p.plan_type_id = pt.id " +
            "INNER JOIN devices d ON pd.device_id = d.id " +
            "WHERE pt.code = 'MAINTENANCE' AND (pr.status IS NULL OR pr.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId) " +
            "AND (:year IS NULL OR :year = 0 OR YEAR(COALESCE(pr.date_test, pr.created_at)) = :year)",
            nativeQuery = true)
    long countMaintenanceExecutions(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("year") Integer year);

    @Query(value = "SELECT COUNT(DISTINCT a.device_id) FROM acceptances a " +
            "INNER JOIN devices d ON a.device_id = d.id " +
            "WHERE a.type = 2 AND a.status = 3 " +
            "AND (d.status IS NULL OR d.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId)",
            nativeQuery = true)
    long countAcceptedMaintenanceDevices(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId);

    // Muc 6: devices.status = 3 (ConstantStatusGlobal.APPROVED)
    @Query("SELECT COUNT(d) FROM Device d WHERE d.status = 3 " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId)")
    long countFaultyDevicesByStatus(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId);

    // Muc 7 cach 1: bang report_device_incidents
    @Query(value = "SELECT COUNT(*) FROM report_device_incidents rdi " +
            "INNER JOIN devices d ON rdi.device_id = d.id " +
            "WHERE (rdi.status IS NULL OR rdi.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId)",
            nativeQuery = true)
    long countCriticalIncidentReports(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId);

    @Query("SELECT d FROM Device d " +
           "WHERE d.state = 1 AND (d.status IS NULL OR d.status <> 10) " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId) " +
           "AND (:deviceName IS NULL OR :deviceName = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :deviceName, '%'))) " +
           "ORDER BY d.id DESC")
    Page<Device> findWorkingDevices(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("deviceName") String deviceName,
            Pageable pageable);

    @Query("SELECT d FROM Device d WHERE d.status = 3 " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId) " +
           "AND (:deviceName IS NULL OR :deviceName = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :deviceName, '%'))) " +
           "ORDER BY d.id DESC")
    Page<Device> findFaultyDevicesByStatus(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("deviceName") String deviceName,
            Pageable pageable);

    @Query("SELECT pd FROM PlanDetail pd " +
           "JOIN FETCH pd.plan p JOIN FETCH p.planType pt " +
           "LEFT JOIN FETCH pd.device d LEFT JOIN FETCH d.branch " +
           "LEFT JOIN FETCH d.team LEFT JOIN FETCH d.line " +
           "WHERE pt.code = 'MAINTENANCE' AND p.status = 4 " +
           "AND (pd.status IS NULL OR pd.status <> 10) AND (d.status IS NULL OR d.status <> 10) " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId) " +
           "AND pd.estimatedTime IS NOT NULL " +
           "AND YEAR(pd.estimatedTime) = :year AND MONTH(pd.estimatedTime) = :month " +
           "AND (:deviceName IS NULL OR :deviceName = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :deviceName, '%')))")
    Page<PlanDetail> findMonthlyMaintenancePlanDetails(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("year") int year,
            @Param("month") int month,
            @Param("deviceName") String deviceName,
            Pageable pageable);

    @Query(value = "SELECT pr.id, pd.id, d.id, d.code, d.name, p.name, " +
            "pr.date_test, pr.status, b.name, t.name, l.name FROM plan_results pr " +
            "INNER JOIN plan_details pd ON pr.plan_detail_id = pd.id " +
            "INNER JOIN plans p ON pd.plan_id = p.id " +
            "INNER JOIN plan_types pt ON p.plan_type_id = pt.id " +
            "INNER JOIN devices d ON pd.device_id = d.id " +
            "LEFT JOIN branches b ON d.branch_id = b.id " +
            "LEFT JOIN teams t ON d.team_id = t.id " +
            "LEFT JOIN `lines` l ON d.line_id = l.id " +
            "WHERE pt.code = 'MAINTENANCE' AND (pr.status IS NULL OR pr.status <> 10) " +
            "AND (d.status IS NULL OR d.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId) " +
            "AND (:year IS NULL OR :year = 0 OR YEAR(COALESCE(pr.date_test, pr.created_at)) = :year) " +
            "AND (:deviceName IS NULL OR :deviceName = '' OR d.name LIKE CONCAT('%', :deviceName, '%')) " +
            "ORDER BY pr.id DESC",
            countQuery = "SELECT COUNT(pr.id) FROM plan_results pr " +
            "INNER JOIN plan_details pd ON pr.plan_detail_id = pd.id " +
            "INNER JOIN plans p ON pd.plan_id = p.id " +
            "INNER JOIN plan_types pt ON p.plan_type_id = pt.id " +
            "INNER JOIN devices d ON pd.device_id = d.id " +
            "WHERE pt.code = 'MAINTENANCE' AND (pr.status IS NULL OR pr.status <> 10) " +
            "AND (d.status IS NULL OR d.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId) " +
            "AND (:year IS NULL OR :year = 0 OR YEAR(COALESCE(pr.date_test, pr.created_at)) = :year) " +
            "AND (:deviceName IS NULL OR :deviceName = '' OR d.name LIKE CONCAT('%', :deviceName, '%'))",
            nativeQuery = true)
    Page<Object[]> findMaintenanceExecutions(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("year") Integer year,
            @Param("deviceName") String deviceName,
            Pageable pageable);

    @Query("SELECT a FROM Acceptance a JOIN FETCH a.device d " +
           "LEFT JOIN FETCH d.branch LEFT JOIN FETCH d.team " +
           "WHERE a.type = 2 AND a.status = 3 AND (d.status IS NULL OR d.status <> 10) " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId) " +
           "AND (:deviceName IS NULL OR :deviceName = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :deviceName, '%'))) " +
           "ORDER BY a.id DESC")
    Page<Acceptance> findAcceptedMaintenance(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("deviceName") String deviceName,
            Pageable pageable);

    @Query("SELECT rdi FROM ReportDeviceIncident rdi JOIN FETCH rdi.device d " +
           "LEFT JOIN FETCH d.branch LEFT JOIN FETCH d.team JOIN FETCH rdi.errorReport " +
           "WHERE (rdi.status IS NULL OR rdi.status <> 10) " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId) " +
           "AND (:deviceName IS NULL OR :deviceName = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :deviceName, '%'))) " +
           "ORDER BY rdi.id DESC")
    Page<ReportDeviceIncident> findCriticalIncidentReports(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("deviceName") String deviceName,
            Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM (SELECT d.id FROM devices d " +
            "LEFT JOIN (SELECT pd.device_id, pr.date_test, " +
            "ROW_NUMBER() OVER (PARTITION BY pd.device_id ORDER BY pr.id DESC) rn " +
            "FROM plan_results pr JOIN plan_details pd ON pr.plan_detail_id = pd.id " +
            "JOIN plans p ON pd.plan_id = p.id WHERE p.plan_type_id = 5) lr " +
            "ON d.id = lr.device_id AND lr.rn = 1 " +
            "WHERE (d.status IS NULL OR d.status <> 10) " +
            "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch_id IN (:branchIds)) " +
            "AND (:teamId IS NULL OR d.team_id = :teamId) " +
            "AND (:groupId IS NULL OR d.group_id = :groupId) " +
            "AND ((:filterType = 'OVERDUE' AND (DATE_ADD(lr.date_test, INTERVAL d.maintenance_time MONTH) < CURDATE() OR lr.date_test IS NULL)) " +
            "OR (:filterType = 'UPCOMING' AND DATE_ADD(lr.date_test, INTERVAL d.maintenance_time MONTH) >= CURDATE()) " +
            "OR (:filterType = 'ALL')) ) t",
            nativeQuery = true)
    long countDueMaintenanceDevices(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("filterType") String filterType);

    @Query("SELECT pd FROM PlanDetail pd " +
           "JOIN FETCH pd.plan p JOIN FETCH p.planType pt " +
           "LEFT JOIN FETCH pd.device d LEFT JOIN FETCH d.branch " +
           "LEFT JOIN FETCH d.team LEFT JOIN FETCH d.line " +
           "WHERE pt.code = 'MAINTENANCE' AND p.status = 4 " +
           "AND (pd.status IS NULL OR pd.status <> 10) AND (d.status IS NULL OR d.status <> 10) " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId) " +
           "AND (:year IS NULL OR :year = 0 OR YEAR(COALESCE(p.fromDate, pd.updatedAt)) = :year) " +
           "AND (:deviceName IS NULL OR :deviceName = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :deviceName, '%')))")
    Page<PlanDetail> findYearlyMaintenancePlanDetails(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("year") int year,
            @Param("deviceName") String deviceName,
            Pageable pageable);

    @Query("SELECT pd FROM PlanDetail pd " +
           "JOIN FETCH pd.plan p JOIN FETCH p.planType pt " +
           "LEFT JOIN FETCH pd.device d LEFT JOIN FETCH d.branch " +
           "LEFT JOIN FETCH d.team LEFT JOIN FETCH d.line " +
           "WHERE pt.code = 'MAINTENANCE' AND pd.status = 5 " +
           "AND (p.status IS NULL OR p.status <> 10) AND (d.status IS NULL OR d.status <> 10) " +
           "AND (COALESCE(:branchIds, NULL) IS NULL OR d.branch.id IN :branchIds) " +
           "AND (:teamId IS NULL OR d.team.id = :teamId) " +
           "AND (:groupId IS NULL OR d.group.id = :groupId) " +
           "AND (:deviceName IS NULL OR :deviceName = '' OR LOWER(d.name) LIKE LOWER(CONCAT('%', :deviceName, '%')))")
    Page<PlanDetail> findMaintainedPlanDetails(
            @Param("branchIds") java.util.List<Long> branchIds,
            @Param("teamId") Long teamId,
            @Param("groupId") Long groupId,
            @Param("deviceName") String deviceName,
            Pageable pageable);

}