package io.rd.qltb.repos;

import io.rd.qltb.domain.SampleReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SampleReportRepository extends JpaRepository<SampleReport, Long> {

    SampleReport findFirstByDeviceGroupId(Long id);
    List<SampleReport> findAllByStatusNotOrderByIdDesc(Integer status);
    List<SampleReport> findAllByStatusOrderByIdDesc(Integer status);

}
