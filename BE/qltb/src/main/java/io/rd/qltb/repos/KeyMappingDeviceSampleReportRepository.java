package io.rd.qltb.repos;

import io.rd.qltb.domain.KeyMappingDeviceSampleReport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KeyMappingDeviceSampleReportRepository extends JpaRepository<KeyMappingDeviceSampleReport, Long> {

    KeyMappingDeviceSampleReport findFirstBySampleReportId(Long id);

    KeyMappingDeviceSampleReport findFirstByDeviceGroupId(Long id);

}
