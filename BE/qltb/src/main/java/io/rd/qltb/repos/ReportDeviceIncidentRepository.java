package io.rd.qltb.repos;

import io.rd.qltb.domain.ReportDeviceIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportDeviceIncidentRepository extends JpaRepository<ReportDeviceIncident, Long> {
    List<ReportDeviceIncident> findAllByStatusNotOrderByIdDesc(Integer status);
}
