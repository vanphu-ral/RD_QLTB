package io.rd.qltb.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.events.BeforeDeleteSampleReport;
import io.rd.qltb.model.DetailLogDTO;
import io.rd.qltb.model.KeyMappingDTO;
import io.rd.qltb.model.SampleReportDTO;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SampleReportService {

    private final SampleReportRepository sampleReportRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final BranchRepository branchRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final ApplicationEventPublisher publisher;
    private final DetailLogService detailLogService;
    private final DetailLogRepository detailLogRepository;
    private final KeyMappingService keyMappingService;
    public SampleReportService(final SampleReportRepository sampleReportRepository,
                               final DeviceGroupRepository deviceGroupRepository,
                               final BranchRepository branchRepository,
                               final ApprovalWorkflowRepository approvalWorkflowRepository,
                               final ApplicationEventPublisher publisher, DetailLogService detailLogService, DetailLogRepository detailLogRepository, KeyMappingService keyMappingService) {
        this.sampleReportRepository = sampleReportRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.branchRepository = branchRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.publisher = publisher;
        this.detailLogService = detailLogService;
        this.detailLogRepository = detailLogRepository;
        this.keyMappingService = keyMappingService;
    }

    public List<SampleReportDTO> findAll() {
        final List<SampleReport> sampleReports = sampleReportRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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

    public void update(final Long id, final SampleReportDTO sampleReportDTO, String userName) {
        try {
            // 1. Lấy entity gốc từ DB
            final SampleReport sampleReport = sampleReportRepository.findById(id)
                    .orElseThrow(NotFoundException::new);

            // 2. Map sang DTO để tạo snapshot trước khi update
            SampleReportDTO beforeUpdateDTO = mapToDTO(sampleReport, new SampleReportDTO());
            List<KeyMappingDTO> keyMappingDTOS = keyMappingService.getBySampleReportId(id);
            beforeUpdateDTO.setSampleReportKeyMappings(keyMappingDTOS);
            // 3. Convert snapshot sang JSON (log dữ liệu cũ)
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            String snapshotJson = mapper.writeValueAsString(beforeUpdateDTO);

            // 4. Tạo bản ghi log
            Integer countLog = detailLogRepository.countByEntityTypeAndEntityId("sample_reports", id);
            DetailLogDTO detailLog = new DetailLogDTO();
            detailLog.setEntityType("sample_reports");
            detailLog.setEntityId(id);
            detailLog.setDetail(snapshotJson); // dữ liệu trước update
            detailLog.setVersion(String.valueOf(countLog + 1));
            detailLog.setCreatedAt(LocalDateTime.now());
            detailLog.setLoggedAt(LocalDateTime.now());
            detailLog.setCreatedBy(userName);
            detailLog.setStatus(1);

            // 5. Lưu log trước khi thay đổi entity
            detailLogService.create(detailLog);

            // 6. Áp dữ liệu mới vào entity
            mapToEntity(sampleReportDTO, sampleReport);

            // 7. Lưu entity đã update
            sampleReportRepository.save(sampleReport);

        } catch (Exception e) {
            throw new RuntimeException("Error while processing SampleReport update", e);
        }
    }


    public void delete(final Long id) {
        final SampleReport sampleReport = sampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteSampleReport(id));
        sampleReportRepository.delete(sampleReport);
    }

    public SampleReportDTO mapToDTO(final SampleReport sampleReport,
                                     final SampleReportDTO dto) {
        dto.setId(sampleReport.getId());
        dto.setCode(sampleReport.getCode());
        dto.setName(sampleReport.getName());
        dto.setFrequency(sampleReport.getFrequency());
        dto.setType(sampleReport.getType());
        dto.setDocumentNumber(sampleReport.getDocumentNumber());
        dto.setFormCode(sampleReport.getFormCode());
        dto.setNumberOfIssuances(sampleReport.getNumberOfIssuances());
        dto.setDescription(sampleReport.getDescription());
        dto.setCreatedAt(sampleReport.getCreatedAt());
        dto.setUpdatedAt(sampleReport.getUpdatedAt());
        dto.setCreatedBy(sampleReport.getCreatedBy());
        dto.setUpdatedBy(sampleReport.getUpdatedBy());
        dto.setStatus(sampleReport.getStatus());


        // Sao chép DeviceGroup có kiểm soát
        if (sampleReport.getDeviceGroup() != null) {
            DeviceGroup groupCopy = new DeviceGroup();
            groupCopy.setId(sampleReport.getDeviceGroup().getId());
            groupCopy.setCode(sampleReport.getDeviceGroup().getCode());
            groupCopy.setName(sampleReport.getDeviceGroup().getName());
            groupCopy.setDescription(sampleReport.getDeviceGroup().getDescription());
            groupCopy.setCreatedAt(sampleReport.getDeviceGroup().getCreatedAt());
            groupCopy.setUpdatedAt(sampleReport.getDeviceGroup().getUpdatedAt());
            groupCopy.setCreatedBy(sampleReport.getDeviceGroup().getCreatedBy());
            groupCopy.setUpdatedBy(sampleReport.getDeviceGroup().getUpdatedBy());
            groupCopy.setStatus(sampleReport.getDeviceGroup().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            groupCopy.setDeviceGroupSampleReports(null);
            groupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);
            groupCopy.setGroupDevices(null);
            groupCopy.setDeviceGroupPlanDetails(null);

            dto.setDeviceGroup(groupCopy);
        } else {
            dto.setDeviceGroup(null);
        }

        if (sampleReport.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(sampleReport.getBranch().getId());
            branchCopy.setCode(sampleReport.getBranch().getCode());
            branchCopy.setName(sampleReport.getBranch().getName());
            branchCopy.setDescription(sampleReport.getBranch().getDescription());
            branchCopy.setCreatedAt(sampleReport.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(sampleReport.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(sampleReport.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(sampleReport.getBranch().getUpdatedBy());
            branchCopy.setStatus(sampleReport.getBranch().getStatus());
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            dto.setBranch(branchCopy);
        } else {
            dto.setBranch(null);
        }

        if (sampleReport.getApprovalWorkflow() != null) {
            ApprovalWorkflow workflowCopy = new ApprovalWorkflow();
            workflowCopy.setId(sampleReport.getApprovalWorkflow().getId());
            workflowCopy.setCode(sampleReport.getApprovalWorkflow().getCode());
            workflowCopy.setName(sampleReport.getApprovalWorkflow().getName());
            workflowCopy.setDescription(sampleReport.getApprovalWorkflow().getDescription());
            workflowCopy.setCreatedAt(sampleReport.getApprovalWorkflow().getCreatedAt());
            workflowCopy.setUpdatedAt(sampleReport.getApprovalWorkflow().getUpdatedAt());
            workflowCopy.setCreatedBy(sampleReport.getApprovalWorkflow().getCreatedBy());
            workflowCopy.setUpdatedBy(sampleReport.getApprovalWorkflow().getUpdatedBy());
            workflowCopy.setStatus(sampleReport.getApprovalWorkflow().getStatus());

            // tránh vòng lặp
            workflowCopy.setWorkflowApprovalGroups(null);

            dto.setApprovalWorkflow(workflowCopy);
        } else {
            dto.setApprovalWorkflow(null);
        }

        return dto;
    }


    private SampleReport mapToEntity(final SampleReportDTO sampleReportDTO,
            final SampleReport sampleReport) {
        sampleReport.setCode(sampleReportDTO.getCode());
        sampleReport.setName(sampleReportDTO.getName());
        sampleReport.setFrequency(sampleReportDTO.getFrequency());
        sampleReport.setType(sampleReportDTO.getType());
        sampleReport.setDocumentNumber(sampleReportDTO.getDocumentNumber());
        sampleReport.setFormCode(sampleReportDTO.getFormCode());
        sampleReport.setNumberOfIssuances(sampleReportDTO.getNumberOfIssuances());
        sampleReport.setDescription(sampleReportDTO.getDescription());
        sampleReport.setCreatedAt(sampleReportDTO.getCreatedAt());
        sampleReport.setUpdatedAt(sampleReportDTO.getUpdatedAt());
        sampleReport.setCreatedBy(sampleReportDTO.getCreatedBy());
        sampleReport.setUpdatedBy(sampleReportDTO.getUpdatedBy());
        sampleReport.setStatus(sampleReportDTO.getStatus());
        final DeviceGroup deviceGroup = sampleReportDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(sampleReportDTO.getDeviceGroup().getId())
                .orElseThrow(() -> new NotFoundException("deviceGroup not found"));
        sampleReport.setDeviceGroup(deviceGroup);

        final Branch branch = sampleReportDTO.getBranch() == null ? null :
                branchRepository.findById(sampleReportDTO.getBranch().getId())
                        .orElseThrow(() -> new NotFoundException("branch not found"));
        sampleReport.setBranch(branch);

        final ApprovalWorkflow approvalWorkflow = sampleReportDTO.getApprovalWorkflow() == null ? null :
                approvalWorkflowRepository.findById(sampleReportDTO.getApprovalWorkflow().getId())
                        .orElseThrow(() -> new NotFoundException("approvalWorkflow not found"));
        sampleReport.setApprovalWorkflow(approvalWorkflow);
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
