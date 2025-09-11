package io.rd.qltb.repos;

import io.rd.qltb.domain.SampleReport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SampleReportRepository extends JpaRepository<SampleReport, Integer> {

    SampleReport findFirstByDeviceGroupId(Integer id);

}
