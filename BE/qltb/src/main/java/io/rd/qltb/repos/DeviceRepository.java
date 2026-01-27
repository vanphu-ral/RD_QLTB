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

    List<Device> findByGroupIdAndStatusOrderByIdDesc(Long groupId,Integer status);
    Device findFirstBySerialNumber(String serialNumber);

    Optional<Device> findByQrCode(String qrCode);
    @Query(value = "SELECT distinct(b.id) FROM device_management.devices a \n" +
            "INNER JOIN device_management.device_groups b ON a.group_id = b.id\n" +
            "INNER JOIN device_management.branches c ON c.id = a.branch_id WHERE c.code = ?1 ;",nativeQuery = true)
    List<Long> findDistinctBranchIdsByBranchCode(String branchCode);
    List<Device> findByStatusOrderByIdDesc(Integer status);



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
                pr.*,
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
                lr.id AS plan_result_id
            FROM devices d
            LEFT JOIN LatestResults lr ON d.id = lr.device_id AND lr.rn = 1
        )
        SELECT 
            device_id, device_name, maintenance_time, date_test, estimated_time, 
            next_test, plan_name, plan_result_id,
            DATEDIFF(next_test, CURRENT_DATE) AS days_until_next
        FROM FinalData
        WHERE next_test >= CURRENT_DATE 
          AND (:searchText IS NULL OR device_name LIKE %:searchText%)
        """,
            countQuery = "SELECT count(*) FROM devices d", // Đơn giản hóa count cho native query
            nativeQuery = true)
    Page<Object[]> findDevicesNotYetDue(String searchText, Pageable pageable);
    @Query(value = """
        WITH LatestResults AS (
            SELECT pr.*, pd.device_id, p.name AS plan_name, pd.estimated_time,
                   ROW_NUMBER() OVER (PARTITION BY pd.device_id ORDER BY pr.id DESC) as rn
            FROM plan_results pr
            INNER JOIN plan_details pd ON pr.plan_detail_id = pd.id
            INNER JOIN plans p ON pd.plan_id = p.id
            WHERE p.plan_type_id = 5
        ),
        FinalData AS (
            SELECT d.id AS device_id, d.name AS device_name, d.maintenance_time,
                   lr.date_test, lr.estimated_time,
                   DATE_ADD(lr.date_test, INTERVAL d.maintenance_time MONTH) AS next_test,
                   lr.plan_name, lr.id AS plan_result_id
            FROM devices d
            LEFT JOIN LatestResults lr ON d.id = lr.device_id AND lr.rn = 1
        )
        SELECT *, DATEDIFF(CURRENT_DATE, next_test) AS days_diff
        FROM FinalData
        WHERE (:search IS NULL OR device_name LIKE %:search%)
          AND (
            (:filterType = 'OVERDUE' AND (next_test < CURRENT_DATE OR next_test IS NULL))
            OR (:filterType = 'UPCOMING' AND next_test >= CURRENT_DATE)
            OR (:filterType = 'ALL')
          )
        ORDER BY (next_test IS NULL) DESC, next_test ASC
        """,
            countQuery = "SELECT count(*) FROM devices WHERE (:search IS NULL OR name LIKE %:search%)",
            nativeQuery = true)
    Page<Object[]> findMaintenanceData(@Param("search") String search,
                                       @Param("filterType") String filterType,
                                       Pageable pageable);
}
