package io.rd.qltb.repos;

import io.rd.qltb.domain.ReportDeviceIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportDeviceIncidentRepository extends JpaRepository<ReportDeviceIncident, Long> {
}
