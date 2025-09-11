package io.rd.qltb.service;

import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.KeyMappingDeviceSampleReport;
import io.rd.qltb.domain.SampleReport;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.events.BeforeDeleteSampleReport;
import io.rd.qltb.model.KeyMappingDeviceSampleReportDTO;
import io.rd.qltb.repos.DeviceGroupRepository;
import io.rd.qltb.repos.KeyMappingDeviceSampleReportRepository;
import io.rd.qltb.repos.SampleReportRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
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

    public KeyMappingDeviceSampleReportDTO get(final Integer id) {
        return keyMappingDeviceSampleReportRepository.findById(id)
                .map(keyMappingDeviceSampleReport -> mapToDTO(keyMappingDeviceSampleReport, new KeyMappingDeviceSampleReportDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final KeyMappingDeviceSampleReportDTO keyMappingDeviceSampleReportDTO) {
        final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport = new KeyMappingDeviceSampleReport();
        mapToEntity(keyMappingDeviceSampleReportDTO, keyMappingDeviceSampleReport);
        return keyMappingDeviceSampleReportRepository.save(keyMappingDeviceSampleReport).getId();
    }

    public void update(final Integer id,
            final KeyMappingDeviceSampleReportDTO keyMappingDeviceSampleReportDTO) {
        final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport = keyMappingDeviceSampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(keyMappingDeviceSampleReportDTO, keyMappingDeviceSampleReport);
        keyMappingDeviceSampleReportRepository.save(keyMappingDeviceSampleReport);
    }

    public void delete(final Integer id) {
        final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport = keyMappingDeviceSampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        keyMappingDeviceSampleReportRepository.delete(keyMappingDeviceSampleReport);
    }

    private KeyMappingDeviceSampleReportDTO mapToDTO(
            final KeyMappingDeviceSampleReport keyMappingDeviceSampleReport,
            final KeyMappingDeviceSampleReportDTO dto) {

        dto.setId(keyMappingDeviceSampleReport.getId());

        // Sao chép SampleReport có kiểm soát
        if (keyMappingDeviceSampleReport.getSampleReport() != null) {
            SampleReport sampleReportCopy = new SampleReport();
            sampleReportCopy.setId(keyMappingDeviceSampleReport.getSampleReport().getId());
            sampleReportCopy.setCode(keyMappingDeviceSampleReport.getSampleReport().getCode());
            sampleReportCopy.setName(keyMappingDeviceSampleReport.getSampleReport().getName());
            sampleReportCopy.setFrequency(keyMappingDeviceSampleReport.getSampleReport().getFrequency());
            sampleReportCopy.setType(keyMappingDeviceSampleReport.getSampleReport().getType());
            sampleReportCopy.setCreatedAt(keyMappingDeviceSampleReport.getSampleReport().getCreatedAt());
            sampleReportCopy.setUpdatedAt(keyMappingDeviceSampleReport.getSampleReport().getUpdatedAt());
            sampleReportCopy.setCreatedBy(keyMappingDeviceSampleReport.getSampleReport().getCreatedBy());
            sampleReportCopy.setUpdatedBy(keyMappingDeviceSampleReport.getSampleReport().getUpdatedBy());
            sampleReportCopy.setStatus(keyMappingDeviceSampleReport.getSampleReport().getStatus());

            // Xóa các quan hệ con
            sampleReportCopy.setDeviceGroup(null);
            sampleReportCopy.setSampleReportKeyMappingDeviceSampleReports(null);
            sampleReportCopy.setSampleReportKeyMappings(null);

            dto.setSampleReport(sampleReportCopy);
        } else {
            dto.setSampleReport(null);
        }

        // Sao chép DeviceGroup có kiểm soát
        if (keyMappingDeviceSampleReport.getDeviceGroup() != null) {
            DeviceGroup groupCopy = new DeviceGroup();
            groupCopy.setId(keyMappingDeviceSampleReport.getDeviceGroup().getId());
            groupCopy.setCode(keyMappingDeviceSampleReport.getDeviceGroup().getCode());
            groupCopy.setName(keyMappingDeviceSampleReport.getDeviceGroup().getName());
            groupCopy.setDescription(keyMappingDeviceSampleReport.getDeviceGroup().getDescription());
            groupCopy.setCreatedAt(keyMappingDeviceSampleReport.getDeviceGroup().getCreatedAt());
            groupCopy.setUpdatedAt(keyMappingDeviceSampleReport.getDeviceGroup().getUpdatedAt());
            groupCopy.setCreatedBy(keyMappingDeviceSampleReport.getDeviceGroup().getCreatedBy());
            groupCopy.setUpdatedBy(keyMappingDeviceSampleReport.getDeviceGroup().getUpdatedBy());
            groupCopy.setStatus(keyMappingDeviceSampleReport.getDeviceGroup().getStatus());

            // Xóa các quan hệ con
            groupCopy.setDeviceGroupSampleReports(null);
            groupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);
            groupCopy.setGroupDevices(null);
            groupCopy.setDeviceGroupPlanDetails(null);

            dto.setDeviceGroup(groupCopy);
        } else {
            dto.setDeviceGroup(null);
        }

        return dto;
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
