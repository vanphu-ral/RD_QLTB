package io.rd.qltb.repos;

import io.rd.qltb.domain.ErrorReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {

    ErrorReport findFirstByPlanResultId(Long id);
    List<ErrorReport> findByPlanResultId(Long id);
    @Query(value = "select a.* from error_reports a " +
            "inner join plan_results b on b.id = a.plan_result_id " +
            "inner join plan_details c on c.id =b.plan_detail_id where c.id =?1 ", nativeQuery = true)
    List<ErrorReport> findByPlanDetailId(Long id);

    @Query("SELECT er FROM ErrorReport er " +
            "JOIN er.planResult pr " +
            "JOIN pr.planDetail pd " +
            "WHERE pd.device.id = :deviceId " +
            "AND er.timeReported BETWEEN :fromDate AND :toDate")
    Page<ErrorReport> findDetailErrors(
            @Param("deviceId") Long deviceId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT er FROM ErrorReport er " +
            "JOIN er.planResult pr " +
            "JOIN pr.planDetail pd " +
            "WHERE pd.device.id = :deviceId " +
            "AND er.timeReported BETWEEN :fromDate AND :toDate")
    List<ErrorReport> findDetailErrorsList(
            @Param("deviceId") Long deviceId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query("SELECT er FROM ErrorReport er " +
            "JOIN er.planResult pr " +
            "JOIN pr.planDetail pd " +
            "JOIN pd.device d " +
            "LEFT JOIN d.branch b " +
            "WHERE er.severity = 0 " +
            "AND er.isRepaired = true " +
            "AND (er.status IS NULL OR er.status != 10) " +
            "AND (:branch IS NULL OR :branch = '' OR LOWER(b.name) = LOWER(:branch)) " +
            "AND NOT EXISTS (SELECT 1 FROM ReportDeviceIncident r WHERE r.errorReport.id = er.id AND (r.status IS NULL OR r.status != 10)) " +
            "ORDER BY er.timeRepaired DESC, er.id DESC")
    List<ErrorReport> findUnrecordedSeriousErrors(@Param("branch") String branch);

    @Query("SELECT er FROM ErrorReport er " +
            "JOIN er.planResult pr " +
            "JOIN pr.planDetail pd " +
            "WHERE pd.device.id = :deviceId " +
            "AND er.isRepaired = true " +
            "AND (er.status IS NULL OR er.status != 10) " +
            "AND (:year IS NULL OR YEAR(er.timeRepaired) = :year OR (er.timeRepaired IS NULL AND YEAR(er.timeReported) = :year)) " +
            "ORDER BY er.timeRepaired ASC, er.timeReported ASC")
    List<ErrorReport> findRepairsByDeviceAndYear(
            @Param("deviceId") Long deviceId,
            @Param("year") Integer year);
}
