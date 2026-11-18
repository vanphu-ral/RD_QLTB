package io.rd.qltb.repos;

import io.rd.qltb.domain.ReportDeviceIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportDeviceIncidentRepository extends JpaRepository<ReportDeviceIncident, Long> {
    List<ReportDeviceIncident> findAllByStatusNotOrderByIdDesc(Integer status);

    @Query("SELECT r FROM ReportDeviceIncident r " +
           "LEFT JOIN FETCH r.device d " +
           "LEFT JOIN FETCH d.branch b " +
           "WHERE (r.status IS NULL OR r.status != :status) " +
           "AND (:branch IS NULL OR :branch = '' " +
           "     OR LOWER(b.name) LIKE LOWER(CONCAT('%', :branch, '%')) " +
           "     OR LOWER(b.code) LIKE LOWER(CONCAT('%', :branch, '%')) " +
           "     OR LOWER(:branch) LIKE LOWER(CONCAT('%', b.name, '%'))) " +
           "ORDER BY r.id DESC")
    List<ReportDeviceIncident> findAllByStatusNotAndBranch(@Param("status") Integer status, @Param("branch") String branch);

    Optional<ReportDeviceIncident> findByErrorReportId(Long errorReportId);
    Optional<ReportDeviceIncident> findFirstByStatusAndFormCodeIsNotNullOrderByIdDesc(Integer status);
}
