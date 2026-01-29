package io.rd.qltb.repos;

import io.rd.qltb.domain.SupplyReplacementHistory;
import io.rd.qltb.model.response.ReportSupplyResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface SupplyReplacementHistoryRepository extends JpaRepository<SupplyReplacementHistory, Long> {

    SupplyReplacementHistory findFirstByOldSupplyDetailId(Long id);

    SupplyReplacementHistory findFirstByNewSupplyDetailId(Long id);

    // Lấy theo planResultId, sắp xếp theo createdAt tăng dần
    List<SupplyReplacementHistory> findByPlanResultIdOrderByCreatedAtAsc(Long planResultId);
    List<SupplyReplacementHistory> findByDeviceIdOrderByCreatedAtDesc(Long deviceId);
    @Query(value = "SELECT \n" +
            "    DATE_FORMAT(sr.created_at, '%Y-%m-%d') as createdAt,\n" +
            "    sr.quantity as quantity,\n" +
            "    s.name as supplyName,\n" +
            "    s.sap_code as sapCode,\n" +
            "    sd.serial as supplySerial,\n" +
            "    sd.unit as supplyUnit,\n" +
            "    sd.price as supplyPrice,\n" +
            "    sd.currency as supplyCurrency,\n" +
            "    l.name as lineName,\n" +
            "    b.id as id ,\n" +
            "    t.id as teamId\n" +
            "FROM device_management.supply_replacements sr\n" +
            "INNER JOIN device_management.supply_details sd ON sd.id = sr.supply_detail_id\n" +
            "INNER JOIN device_management.supplies s ON s.id = sd.supply_id\n" +
            "INNER JOIN device_management.plan_results pr ON pr.id = sr.plan_result_id\n" +
            "INNER JOIN device_management.plan_details pd ON pr.plan_detail_id = pd.id\n" +
            "INNER JOIN device_management.plans p ON p.id = pd.plan_id\n" +
            "INNER JOIN device_management.devices d ON d.id = pd.device_id\n" +
            "INNER JOIN device_management.lines l ON l.id = d.line_id\n" +
            "INNER JOIN device_management.branches b ON b.id = p.branch_id\n" +
            "INNER JOIN device_management.teams t ON t.id = p.team_id\n" +
            "where b.id = ?1 and t.id = ?2 and DATE_FORMAT(sr.created_at, '%Y-%m-%d') between ?3 and ?4 \n" +
            " ;", nativeQuery = true)
    List<ReportSupplyResponse> getSupplyReportByTeamAndDateRange(Long teamId, Long branchId, String startDate, String endDate);



    @Query("SELECT s FROM SupplyReplacementHistory s " +
            "LEFT JOIN FETCH s.oldSupplyDetail " + // Fetch để tránh lỗi Lazy Loading khi map sang DTO
            "LEFT JOIN FETCH s.newSupplyDetail " +
            "WHERE s.deviceId = :deviceId " +
            "AND s.createdAt BETWEEN :fromDate AND :toDate " +
            "ORDER BY s.createdAt DESC")
    Page<SupplyReplacementHistory> findByDeviceIdAndCreatedAtBetween(
            @Param("deviceId") Long deviceId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);


    @Query("SELECT s FROM SupplyReplacementHistory s " +
            "LEFT JOIN FETCH s.oldSupplyDetail " +
            "LEFT JOIN FETCH s.newSupplyDetail " +
            "WHERE s.deviceId = :deviceId " +
            "AND s.createdAt BETWEEN :fromDate AND :toDate " +
            "ORDER BY s.createdAt DESC")
    List<SupplyReplacementHistory> findHistoryByDeviceList(
                                                            @Param("deviceId") Long deviceId,
                                                            @Param("fromDate") LocalDateTime fromDate,
                                                            @Param("toDate") LocalDateTime toDate);
}
