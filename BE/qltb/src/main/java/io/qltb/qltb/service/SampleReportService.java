package io.qltb.qltb.service;

import io.qltb.qltb.domain.DeviceGroup;
import io.qltb.qltb.domain.SampleReport;
import io.qltb.qltb.events.BeforeDeleteDeviceGroup;
import io.qltb.qltb.events.BeforeDeleteSampleReport;
import io.qltb.qltb.model.SampleReportDTO;
import io.qltb.qltb.repos.DeviceGroupRepository;
import io.qltb.qltb.repos.SampleReportRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SampleReportService {

    private final SampleReportRepository sampleReportRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final ApplicationEventPublisher publisher;

    public SampleReportService(final SampleReportRepository sampleReportRepository,
            final DeviceGroupRepository deviceGroupRepository,
            final ApplicationEventPublisher publisher) {
        this.sampleReportRepository = sampleReportRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.publisher = publisher;
    }

    public List<SampleReportDTO> findAll() {
        final List<SampleReport> sampleReports = sampleReportRepository.findAll(Sort.by("id"));
        return sampleReports.stream()
                .map(sampleReport -> mapToDTO(sampleReport, new SampleReportDTO()))
                .toList();
    }

    public SampleReportDTO get(final Long id) {
        return sampleReportRepository.findById(id)
                .map(sampleReport -> mapToDTO(sampleReport, new SampleReportDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SampleReportDTO sampleReportDTO) {
        final SampleReport sampleReport = new SampleReport();
        mapToEntity(sampleReportDTO, sampleReport);
        return sampleReportRepository.save(sampleReport).getId();
    }

    public void update(final Long id, final SampleReportDTO sampleReportDTO) {
        final SampleReport sampleReport = sampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(sampleReportDTO, sampleReport);
        sampleReportRepository.save(sampleReport);
    }

    public void delete(final Long id) {
        final SampleReport sampleReport = sampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteSampleReport(id));
        sampleReportRepository.delete(sampleReport);
    }

    private SampleReportDTO mapToDTO(final SampleReport sampleReport,
            final SampleReportDTO sampleReportDTO) {
        sampleReportDTO.setId(sampleReport.getId());
        sampleReportDTO.setCode(sampleReport.getCode());
        sampleReportDTO.setName(sampleReport.getName());
        sampleReportDTO.setFrequency(sampleReport.getFrequency());
        sampleReportDTO.setType(sampleReport.getType());
        sampleReportDTO.setCreatedAt(sampleReport.getCreatedAt());
        sampleReportDTO.setUpdatedAt(sampleReport.getUpdatedAt());
        sampleReportDTO.setCreatedBy(sampleReport.getCreatedBy());
        sampleReportDTO.setUpdatedBy(sampleReport.getUpdatedBy());
        sampleReportDTO.setStatus(sampleReport.getStatus());
        sampleReportDTO.setDeviceGroup(sampleReport.getDeviceGroup() == null ? null : sampleReport.getDeviceGroup());
        return sampleReportDTO;
    }

    private SampleReport mapToEntity(final SampleReportDTO sampleReportDTO,
            final SampleReport sampleReport) {
        sampleReport.setCode(sampleReportDTO.getCode());
        sampleReport.setName(sampleReportDTO.getName());
        sampleReport.setFrequency(sampleReportDTO.getFrequency());
        sampleReport.setType(sampleReportDTO.getType());
        sampleReport.setCreatedAt(sampleReportDTO.getCreatedAt());
        sampleReport.setUpdatedAt(sampleReportDTO.getUpdatedAt());
        sampleReport.setCreatedBy(sampleReportDTO.getCreatedBy());
        sampleReport.setUpdatedBy(sampleReportDTO.getUpdatedBy());
        sampleReport.setStatus(sampleReportDTO.getStatus());
        final DeviceGroup deviceGroup = sampleReportDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(sampleReportDTO.getDeviceGroup().getId())
                .orElseThrow(() -> new NotFoundException("deviceGroup not found"));
        sampleReport.setDeviceGroup(deviceGroup);
        return sampleReport;
    }

    @EventListener(BeforeDeleteDeviceGroup.class)
    public void on(final BeforeDeleteDeviceGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final SampleReport deviceGroupSampleReport = sampleReportRepository.findFirstByDeviceGroupId(event.getId());
        if (deviceGroupSampleReport != null) {
            referencedException.setKey("deviceGroup.sampleReport.deviceGroup.referenced");
            referencedException.addParam(deviceGroupSampleReport.getId());
            throw referencedException;
        }
    }

}
