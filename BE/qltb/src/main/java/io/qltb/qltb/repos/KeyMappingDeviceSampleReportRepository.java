package io.qltb.qltb.repos;

import io.qltb.qltb.domain.KeyMappingDeviceSampleReport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KeyMappingDeviceSampleReportRepository extends JpaRepository<KeyMappingDeviceSampleReport, Long> {

    KeyMappingDeviceSampleReport findFirstBySampleReportId(Long id);

    KeyMappingDeviceSampleReport findFirstByDeviceGroupId(Long id);

}
