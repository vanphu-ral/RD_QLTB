package io.rd.qltb.repos;

import io.rd.qltb.domain.SampleReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SampleReportRepository extends JpaRepository<SampleReport, Long> {

    @Query("SELECT s FROM SampleReport s JOIN s.deviceGroups g WHERE g.id = :id")
    List<SampleReport> findAllByDeviceGroupId(@Param("id") Long id);

    @Query(value = "SELECT s.* FROM SampleReports s JOIN SampleReportDeviceGroups j ON s.id = j.sample_report_id WHERE j.device_group_id = :id LIMIT 1", nativeQuery = true)
    Optional<SampleReport> findFirstByDeviceGroupId(@Param("id") Long id);
    List<SampleReport> findAllByStatusNotOrderByIdDesc(Integer status);
    List<SampleReport> findAllByStatusOrderByIdDesc(Integer status);

}
