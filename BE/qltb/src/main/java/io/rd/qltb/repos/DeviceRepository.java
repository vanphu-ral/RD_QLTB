package io.rd.qltb.repos;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.Line;
import io.rd.qltb.model.response.DeviceErrorSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findFirstByGroupId(Long id);

    Device findFirstByLineId(Long id);

    Device findFirstByBranchId(Long id);

    Device findFirstByTeamId(Long id);

    List<Device> findByGroupIdAndStatusNotOrderByIdDesc(Long groupId,Integer status);
    Device findFirstByQrCode(String qrCode);

    Optional<Device> findByQrCode(String qrCode);
    @Query(value = "SELECT distinct(b.id) FROM device_management.devices a \n" +
            "INNER JOIN device_management.device_groups b ON a.group_id = b.id\n" +
            "INNER JOIN device_management.branches c ON c.id = a.branch_id WHERE c.code = ?1 ;",nativeQuery = true)
    List<Long> findDistinctBranchIdsByBranchCode(String branchCode);
    List<Device> findByStatusOrderByIdDesc(Integer status);

    @Query(value= "select count(*) from devices d where d.group_id = :groupId and d.status != :status",nativeQuery = true)
    Long countByGroupIdAndStatusNot(@Param("groupId") Long groupId, @Param("status") Integer status);

    @Query("SELECT new io.rd.qltb.model.response.DeviceErrorSummaryDTO(" +
            "f.name, b.name, t.name, dg.name, " +
            "CAST(YEAR(er.timeReported) AS Integer), " +
            "CAST(MONTH(er.timeReported) AS Integer), " +
            "d.name, d.code, COUNT(er.id), " +
            "SUM(FUNCTION('TIMESTAMPDIFF', SECOND, er.timeReported, er.timeRepaired)) / 3600.0, " +
            "d.id) " +
            "FROM ErrorReport er " +
            "JOIN er.planResult pr " +
            "JOIN pr.planDetail pd " +
            "JOIN pd.device d " +
            "LEFT JOIN d.branch b " +
            "LEFT JOIN d.team t " +
            "LEFT JOIN d.group dg " +
            "LEFT JOIN pd.plan p " +
            "LEFT JOIN p.factory f " +
            "WHERE (COALESCE(:branchIds, NULL) IS NULL OR b.id IN :branchIds) " + // Dùng IN
            "AND (COALESCE(:groupIds, NULL) IS NULL OR dg.id IN :groupIds) " + // Dùng IN
            "AND (COALESCE(:teamIds, NULL) IS NULL OR t.id IN :teamIds) " +
            "AND (er.timeReported BETWEEN :fromDate AND :toDate) " +
            "GROUP BY f.name, b.name, t.name, YEAR(er.timeReported), MONTH(er.timeReported), d.name, d.code, d.id")
    Page<DeviceErrorSummaryDTO> getErrorSummaryReport(
            @Param("branchIds") List<Long> branchIds, // Đổi thành List
            @Param("teamIds") List<Long> teamIds,
            @Param("groupIds") List<Long> groupIds,   // Đổi thành List
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
    @Query(value = """
    WITH LatestResults AS (
        SELECT 
            pr.id, 
            pr.date_test,
            pd.device_id,
            p.name AS plan_name,
            pd.estimated_time,
            ROW_NUMBER() OVER (PARTITION BY pd.device_id ORDER BY pr.id DESC) as rn
        FROM plan_results pr
        INNER JOIN plan_details pd ON pr.plan_detail_id = pd.id
        INNER JOIN plans p ON pd.plan_id = p.id
        WHERE p.plan_type_id = 5
    ),
    FinalData AS (
        SELECT 
            d.id AS device_id,
            d.name AS device_name,
            d.maintenance_time,
            lr.date_test,
            lr.estimated_time,
            DATE_ADD(lr.date_test, INTERVAL d.maintenance_time MONTH) AS next_test,
            lr.plan_name,
            lr.id AS plan_result_id,
            dg.name AS device_group_name,
            b.name AS branch_name,
            t.name AS team_name,
            l.name AS line_name
        FROM devices d
        LEFT JOIN LatestResults lr ON d.id = lr.device_id AND lr.rn = 1
        LEFT JOIN device_groups dg ON d.group_id = dg.id
        LEFT JOIN branches b ON d.branch_id = b.id
        LEFT JOIN teams t ON d.team_id = t.id
        LEFT JOIN `lines` l ON d.line_id = l.id
    )
    SELECT 
        device_id, device_name, maintenance_time, date_test, estimated_time, 
        next_test, plan_name, plan_result_id,
        DATEDIFF(next_test, CURDATE()) AS days_until_next,
        device_group_name, branch_name, team_name, line_name
    FROM FinalData
    WHERE next_test >= CURDATE() 
      AND (:deviceName IS NULL OR device_name LIKE CONCAT('%', :deviceName, '%'))
      AND (:groupName IS NULL OR device_group_name LIKE CONCAT('%', :groupName, '%'))
      AND (:lineName IS NULL OR line_name LIKE CONCAT('%', :lineName, '%'))
      AND (:teamName IS NULL OR team_name LIKE CONCAT('%', :teamName, '%'))
    """,
            countQuery = """
    SELECT COUNT(*) FROM (
        SELECT d.id
        FROM devices d
        LEFT JOIN (
            SELECT pd.device_id, MAX(pr.date_test) as last_test
            FROM plan_results pr
            JOIN plan_details pd ON pr.plan_detail_id = pd.id
            JOIN plans p ON pd.plan_id = p.id
            WHERE p.plan_type_id = 5
            GROUP BY pd.device_id
        ) lr ON d.id = lr.device_id
        LEFT JOIN `lines` l ON d.line_id = l.id
        LEFT JOIN device_groups dg ON d.group_id = dg.id
        LEFT JOIN teams t ON d.team_id = t.id
        WHERE DATE_ADD(lr.last_test, INTERVAL d.maintenance_time MONTH) >= CURDATE()
          AND (:deviceName IS NULL OR d.name LIKE CONCAT('%', :deviceName, '%'))
          AND (:groupName IS NULL OR dg.name LIKE CONCAT('%', :groupName, '%'))
          AND (:lineName IS NULL OR l.name LIKE CONCAT('%', :lineName, '%'))
          AND (:branchName IS NULL OR b.name LIKE CONCAT('%', :branchName, '%'))
          AND (:teamName IS NULL OR t.name LIKE CONCAT('%', :teamName, '%'))
    ) AS temp_count
    """,
            nativeQuery = true)
    Page<Object[]> findDevicesNotYetDue(
            @Param("deviceName") String deviceName,
            @Param("groupName") String groupName,
            @Param("lineName") String lineName,
            @Param("branchName") String branchName,
            @Param("teamName") String teamName,
            Pageable pageable
    );
    @Query(value = """
    WITH LatestResults AS (
        SELECT 
            pr.id, pr.date_test, pd.device_id, p.name AS plan_name, pd.estimated_time,
            ROW_NUMBER() OVER (PARTITION BY pd.device_id ORDER BY pr.id DESC) as rn
        FROM plan_results pr
        INNER JOIN plan_details pd ON pr.plan_detail_id = pd.id
        INNER JOIN plans p ON pd.plan_id = p.id
        WHERE p.plan_type_id = 5
    ),
    FinalData AS (
        SELECT 
            d.id AS device_id, d.name AS device_name, d.maintenance_time,
            lr.date_test, lr.estimated_time,
            DATE_ADD(lr.date_test, INTERVAL d.maintenance_time MONTH) AS next_test,
            lr.plan_name, lr.id AS plan_result_id,
            dg.name AS device_group_name,
            b.name AS branch_name,
            t.name AS team_name,
            l.name AS line_name
        FROM devices d
        LEFT JOIN LatestResults lr ON d.id = lr.device_id AND lr.rn = 1
        LEFT JOIN device_groups dg ON d.group_id = dg.id
        LEFT JOIN branches b ON d.branch_id = b.id
        LEFT JOIN teams t ON d.team_id = t.id
        LEFT JOIN `lines` l ON d.line_id = l.id
    )
    SELECT 
        device_id, device_name, maintenance_time, date_test, estimated_time, 
        next_test, plan_name, plan_result_id,
        DATEDIFF(next_test, CURDATE()) AS days_until_next,
        device_group_name, branch_name, team_name, line_name
    FROM FinalData
    WHERE (:deviceName IS NULL OR device_name LIKE CONCAT('%', :deviceName, '%'))
      AND (:branchName IS NULL OR branch_name LIKE CONCAT('%', :branchName, '%'))
      AND (:groupName IS NULL OR device_group_name LIKE CONCAT('%', :groupName, '%'))
      AND (:teamName IS NULL OR team_name LIKE CONCAT('%', :teamName, '%'))
      AND (:lineName IS NULL OR line_name LIKE CONCAT('%', :lineName, '%'))
      AND (
        (:filterType = 'OVERDUE' AND next_test < CURDATE())
        OR (:filterType = 'UPCOMING' AND next_test >= CURDATE())
        OR (:filterType = 'ALL')
      )
    ORDER BY (next_test IS NULL) DESC, next_test ASC
    """,
            countQuery = """
    SELECT COUNT(*) FROM (
        SELECT d.id FROM devices d
        LEFT JOIN (
            SELECT pd.device_id, MAX(pr.date_test) as last_test
            FROM plan_results pr
            JOIN plan_details pd ON pr.plan_detail_id = pd.id
            JOIN plans p ON pd.plan_id = p.id
            WHERE p.plan_type_id = 5
            GROUP BY pd.device_id
        ) lr ON d.id = lr.device_id
        LEFT JOIN branches b ON d.branch_id = b.id
        LEFT JOIN device_groups dg ON d.group_id = dg.id
        LEFT JOIN teams t ON d.team_id = t.id
        LEFT JOIN `lines` l ON d.line_id = l.id
        WHERE (:deviceName IS NULL OR d.name LIKE CONCAT('%', :deviceName, '%'))
          AND (:branchName IS NULL OR b.name LIKE CONCAT('%', :branchName, '%'))
          AND (:groupName IS NULL OR dg.name LIKE CONCAT('%', :groupName, '%'))
          AND (:teamName IS NULL OR t.name LIKE CONCAT('%', :teamName, '%'))
          AND (:lineName IS NULL OR l.name LIKE CONCAT('%', :lineName, '%'))
          AND (
            (:filterType = 'OVERDUE' AND (DATE_ADD(lr.last_test, INTERVAL d.maintenance_time MONTH) < CURDATE() OR lr.last_test IS NULL))
            OR (:filterType = 'UPCOMING' AND DATE_ADD(lr.last_test, INTERVAL d.maintenance_time MONTH) >= CURDATE())
            OR (:filterType = 'ALL')
          )
    ) AS temp_count
    """,
            nativeQuery = true)
    Page<Object[]> findMaintenanceData(
            @Param("deviceName") String deviceName,
            @Param("branchName") String branchName,
            @Param("groupName") String groupName,
            @Param("teamName") String teamName,
            @Param("lineName") String lineName,
            @Param("filterType") String filterType,
            Pageable pageable
    );

}
