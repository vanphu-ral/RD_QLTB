package io.qltb.qltb.repos;

import io.qltb.qltb.domain.SampleReport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SampleReportRepository extends JpaRepository<SampleReport, Long> {

    SampleReport findFirstByDeviceGroupId(Long id);

}
