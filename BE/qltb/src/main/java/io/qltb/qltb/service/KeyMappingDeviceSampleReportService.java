package io.qltb.qltb.service;

import io.qltb.qltb.domain.DeviceGroup;
import io.qltb.qltb.domain.KeyMappingDeviceSampleReport;
import io.qltb.qltb.domain.SampleReport;
import io.qltb.qltb.events.BeforeDeleteDeviceGroup;
import io.qltb.qltb.events.BeforeDeleteSampleReport;
import io.qltb.qltb.model.KeyMappingDeviceSampleReportDTO;
import io.qltb.qltb.repos.DeviceGroupRepository;
import io.qltb.qltb.repos.KeyMappingDeviceSampleReportRepository;
import io.qltb.qltb.repos.SampleReportRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class KeyMappingDeviceSampleReportService {

    private final KeyMappingDeviceSampleReportRepository keyMappingDeviceSampleReportRepository;
    private final SampleReportRepository sampleReportRepository;
    private final DeviceGroupRepository deviceGroupRepository;

    public KeyMappingDeviceSampleReportService(
            final KeyMappingDeviceSampleReportRepository keyMappingDeviceSampleReportRepository,
            final SampleReportRepository sampleReportRepository,
            final DeviceGroupRepository deviceGroupRepository) {
        this.keyMappingDeviceSampleReportRepository = keyMappingDeviceSampleReportRepository;
        this.sampleReportRepository = sampleReportRepository;
        this.deviceGroupRepository = deviceGroupRepository;
    }

    public List<KeyMappingDeviceSampleReportDTO> findAll() {
        final List<KeyMappingDeviceSampleReport> keyMappingDeviceSampleReports = keyMappingDeviceSampleReportRepository.findAll(Sort.by("id"));
        return keyMappingDeviceSampleReports.stream()
                .map(keyMappingDeviceSampleReport -> mapToDTO(keyMappingDeviceSampleReport, new KeyMappingDeviceSampleReportDTO()))
                .toList();
    }

    public KeyMappingDeviceSampleReportDTO get(final Long id) {
        return keyMappingDeviceSampleReportRepository.findById(id)
                .map(keyMappingDeviceSampleReport -> mapToDTO(keyMappingDeviceSampleReport, new KeyMappingDeviceSampleReportDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final KeyMappingDeviceSampleReportDTO keyMappingDeviceSampleReportDTO) {
        final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport = new KeyMappingDeviceSampleReport();
        mapToEntity(keyMappingDeviceSampleReportDTO, keyMappingDeviceSampleReport);
        return keyMappingDeviceSampleReportRepository.save(keyMappingDeviceSampleReport).getId();
    }

    public void update(final Long id,
            final KeyMappingDeviceSampleReportDTO keyMappingDeviceSampleReportDTO) {
        final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport = keyMappingDeviceSampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(keyMappingDeviceSampleReportDTO, keyMappingDeviceSampleReport);
        keyMappingDeviceSampleReportRepository.save(keyMappingDeviceSampleReport);
    }

    public void delete(final Long id) {
        final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport = keyMappingDeviceSampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        keyMappingDeviceSampleReportRepository.delete(keyMappingDeviceSampleReport);
    }

    private KeyMappingDeviceSampleReportDTO mapToDTO(
            final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport,
            final KeyMappingDeviceSampleReportDTO keyMappingDeviceSampleReportDTO) {

        keyMappingDeviceSampleReportDTO.setId(keyMappingDeviceSampleReport.getId());

        // Sao chép SampleReport
        if (keyMappingDeviceSampleReport.getSampleReport() != null) {
            SampleReport sampleReportCopy = new SampleReport();
            sampleReportCopy.setId(keyMappingDeviceSampleReport.getSampleReport().getId());
            sampleReportCopy.setCode(keyMappingDeviceSampleReport.getSampleReport().getCode());
            sampleReportCopy.setName(keyMappingDeviceSampleReport.getSampleReport().getName());
            sampleReportCopy.setStatus(keyMappingDeviceSampleReport.getSampleReport().getStatus());
            sampleReportCopy.setCreatedAt(keyMappingDeviceSampleReport.getSampleReport().getCreatedAt());
            sampleReportCopy.setUpdatedAt(keyMappingDeviceSampleReport.getSampleReport().getUpdatedAt());
            sampleReportCopy.setCreatedBy(keyMappingDeviceSampleReport.getSampleReport().getCreatedBy());
            sampleReportCopy.setUpdatedBy(keyMappingDeviceSampleReport.getSampleReport().getUpdatedBy());

            // Xóa các quan hệ con
            sampleReportCopy.setDeviceGroup(null);
            sampleReportCopy.setSampleReportCriterials(null);
            sampleReportCopy.setSampleReportKeyMappings(null);
            sampleReportCopy.setSampleReportKeyMappingDeviceSampleReports(null);

            keyMappingDeviceSampleReportDTO.setSampleReport(sampleReportCopy);
        } else {
            keyMappingDeviceSampleReportDTO.setSampleReport(null);
        }

        // Sao chép DeviceGroup
        if (keyMappingDeviceSampleReport.getDeviceGroup() != null) {
            DeviceGroup deviceGroupCopy = new DeviceGroup();
            deviceGroupCopy.setId(keyMappingDeviceSampleReport.getDeviceGroup().getId());
            deviceGroupCopy.setCode(keyMappingDeviceSampleReport.getDeviceGroup().getCode());
            deviceGroupCopy.setName(keyMappingDeviceSampleReport.getDeviceGroup().getName());
            deviceGroupCopy.setStatus(keyMappingDeviceSampleReport.getDeviceGroup().getStatus());
            deviceGroupCopy.setCreatedAt(keyMappingDeviceSampleReport.getDeviceGroup().getCreatedAt());
            deviceGroupCopy.setUpdatedAt(keyMappingDeviceSampleReport.getDeviceGroup().getUpdatedAt());
            deviceGroupCopy.setCreatedBy(keyMappingDeviceSampleReport.getDeviceGroup().getCreatedBy());
            deviceGroupCopy.setUpdatedBy(keyMappingDeviceSampleReport.getDeviceGroup().getUpdatedBy());

            // Xóa các quan hệ con
            deviceGroupCopy.setGroupDevices(null);
            deviceGroupCopy.setDeviceGroupSampleReports(null);
            deviceGroupCopy.setDeviceGroupPlanDetails(null);
            deviceGroupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);

            keyMappingDeviceSampleReportDTO.setDeviceGroup(deviceGroupCopy);
        } else {
            keyMappingDeviceSampleReportDTO.setDeviceGroup(null);
        }

        return keyMappingDeviceSampleReportDTO;
    }

    private KeyMappingDeviceSampleReport mapToEntity(
            final KeyMappingDeviceSampleReportDTO keyMappingDeviceSampleReportDTO,
            final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport) {
        final SampleReport sampleReport = keyMappingDeviceSampleReportDTO.getSampleReport() == null ? null : sampleReportRepository.findById(keyMappingDeviceSampleReportDTO.getSampleReport().getId())
                .orElseThrow(() -> new NotFoundException("sampleReport not found"));
        keyMappingDeviceSampleReport.setSampleReport(sampleReport);
        final DeviceGroup deviceGroup = keyMappingDeviceSampleReportDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(keyMappingDeviceSampleReportDTO.getDeviceGroup().getId())
                .orElseThrow(() -> new NotFoundException("deviceGroup not found"));
        keyMappingDeviceSampleReport.setDeviceGroup(deviceGroup);
        return keyMappingDeviceSampleReport;
    }

    @EventListener(BeforeDeleteSampleReport.class)
    public void on(final BeforeDeleteSampleReport event) {
        final ReferencedException referencedException = new ReferencedException();
        final KeyMappingDeviceSampleReport sampleReportKeyMappingDeviceSampleReport = keyMappingDeviceSampleReportRepository.findFirstBySampleReportId(event.getId());
        if (sampleReportKeyMappingDeviceSampleReport != null) {
            referencedException.setKey("sampleReport.keyMappingDeviceSampleReport.sampleReport.referenced");
            referencedException.addParam(sampleReportKeyMappingDeviceSampleReport.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteDeviceGroup.class)
    public void on(final BeforeDeleteDeviceGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final KeyMappingDeviceSampleReport deviceGroupKeyMappingDeviceSampleReport = keyMappingDeviceSampleReportRepository.findFirstByDeviceGroupId(event.getId());
        if (deviceGroupKeyMappingDeviceSampleReport != null) {
            referencedException.setKey("deviceGroup.keyMappingDeviceSampleReport.deviceGroup.referenced");
            referencedException.addParam(deviceGroupKeyMappingDeviceSampleReport.getId());
            throw referencedException;
        }
    }

}
