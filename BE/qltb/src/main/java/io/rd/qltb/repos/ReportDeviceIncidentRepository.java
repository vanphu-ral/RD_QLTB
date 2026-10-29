package io.rd.qltb.repos;

import io.rd.qltb.domain.ReportDeviceIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportDeviceIncidentRepository extends JpaRepository<ReportDeviceIncident, Long> {
    List<ReportDeviceIncident> findAllByStatusNotOrderByIdDesc(Integer status);
    Optional<ReportDeviceIncident> findByErrorReportId(Long errorReportId);
    Optional<ReportDeviceIncident> findFirstByStatusAndFormCodeIsNotNullOrderByIdDesc(Integer status);
}
