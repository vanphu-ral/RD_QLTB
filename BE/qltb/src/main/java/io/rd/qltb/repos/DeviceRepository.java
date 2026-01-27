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
}
