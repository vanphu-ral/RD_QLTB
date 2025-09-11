package io.rd.qltb.repos;

import io.rd.qltb.domain.KeyMappingDeviceSampleReport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KeyMappingDeviceSampleReportRepository extends JpaRepository<KeyMappingDeviceSampleReport, Integer> {

    KeyMappingDeviceSampleReport findFirstBySampleReportId(Integer id);

    KeyMappingDeviceSampleReport findFirstByDeviceGroupId(Integer id);

}
