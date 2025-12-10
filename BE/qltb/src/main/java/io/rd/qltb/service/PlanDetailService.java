package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.events.BeforeDeletePlan;
import io.rd.qltb.model.*;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanDetailService {

    private final PlanDetailRepository planDetailRepository;
    private final PlanRepository planRepository;
    private final DeviceRepository deviceRepository;
    private final SampleReportRepository sampleReportRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final ApprovalRepository approvalRepository;
    private final ApprovalService approvalService;
    private final PlanResultDetailRepository planResultDetailRepository;
    private final PlanResultService planResultService;
    private final PlanResultDetailService planResultDetailService;
    private final PlanResultRepository planResultRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;

    public PlanDetailService(final PlanDetailRepository planDetailRepository,
                             final PlanRepository planRepository, final DeviceRepository deviceRepository,
                             final SampleReportRepository sampleReportRepository,
                             final DeviceGroupRepository deviceGroupRepository, ApprovalRepository approvalRepository, ApprovalService approvalService, PlanResultDetailRepository planResultDetailRepository, PlanResultService planResultService, PlanResultDetailService planResultDetailService, PlanResultRepository planResultRepository, ApprovalWorkflowRepository approvalWorkflowRepository) {
        this.planDetailRepository = planDetailRepository;
        this.planRepository = planRepository;
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.sampleReportRepository = sampleReportRepository;
        this.approvalRepository = approvalRepository;
        this.approvalService = approvalService;
        this.planResultDetailRepository = planResultDetailRepository;
        this.planResultService = planResultService;
        this.planResultDetailService = planResultDetailService;
        this.planResultRepository = planResultRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
    }

    public PlanCheckDTO getPlanCheckDetail(final Long id,String entityType) {
        // Lấy thông tin PlanDetail
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        PlanDetailDTO planDetailDTO = mapToDTO(planDetail, new PlanDetailDTO());
        // Set thêm thông tin liên quan
        planDetailDTO.setSampleReport(planDetail.getSampleReport());
        planDetailDTO.getSampleReport().setDeviceGroup(null);
        planDetailDTO.getSampleReport().setBranch(null);
        planDetailDTO.getSampleReport().setApprovalWorkflow(null);
        planDetailDTO.getSampleReport().setSampleReportKeyMappingDeviceSampleReports(null);
        planDetailDTO.getSampleReport().setSampleReportKeyMappings(null);
        planDetailDTO.getDevice().setBranch(planDetail.getDevice().getBranch());
        planDetailDTO.getDevice().getBranch().getFactory().setFactoryBranches(null);
        planDetailDTO.getDevice().getBranch().setBranchTeams(null);
        planDetailDTO.getDevice().getBranch().setBranchDevices(null);
        planDetailDTO.getDevice().getBranch().setSampleReports(null);
        planDetailDTO.getDevice().setLine(planDetail.getDevice().getLine());
        planDetailDTO.getDevice().getLine().setTeam(null);
        planDetailDTO.getDevice().getLine().setLineDevices(null);
        planDetailDTO.getDevice().setTeam(planDetail.getDevice().getTeam());
        planDetailDTO.getDevice().getTeam().setBranch(null);
        planDetailDTO.getDevice().getTeam().setTeamDevices(null);
        planDetailDTO.getDevice().getTeam().setTeamLines(null);
        PlanCheckDTO planCheckDTO = new PlanCheckDTO();
        planCheckDTO.setPlanDetail(planDetailDTO);
        //  Lấy thông tin Approval dựa trên entityType và entityId
        final List<Approval> approvals = approvalRepository.findByEntityTypeAndEntityId(entityType, id);
        List<ApprovalDTO> approvalDTOS = approvals.stream()
                .map(approval -> approvalService.mapToDTO(approval, new ApprovalDTO()))
                .toList();
        planCheckDTO.setApprovals(approvalDTOS);
        // Lấy thông tin PlanResultDetail dựa trên planDetailId
        List<PlanResultDetail> planResultDetails = planResultDetailRepository.getByPlanDetailId(id);
        List<PlanResultDetailDTO> planResultDetailDTOS = planResultDetails.stream()
                .map(planResultDetail -> planResultDetailService.mapToDTO(planResultDetail, new PlanResultDetailDTO()))
                .toList();
        planCheckDTO.setPlanResultDetail(planResultDetailDTOS);
        return planCheckDTO;
    }
    public List<PlanDetailDTO> findAll() {
        final List<PlanDetail> planDetails = planDetailRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return planDetails.stream()
                .map(planDetail -> mapToDTO(planDetail, new PlanDetailDTO()))
                .toList();
    }
    public List<PlanDTO> getByDetiveId(final String serial) {
        Device device = deviceRepository.findFirstBySerialNumber(serial);
        List<PlanDetailDTO> planDetails = planDetailRepository.findAllByDeviceIdAndStatus(device.getId(),4).stream()
                .map(planDetail -> mapToDTO(planDetail, new PlanDetailDTO()))
                .toList();
        List<PlanDTO> plans = new ArrayList<>();
        for(PlanDetailDTO planDetailDTO : planDetails){
            PlanDTO planDTO = new PlanDTO();
            planDTO.setId(planDetailDTO.getPlan().getId());
            planDTO.setName(planDetailDTO.getPlan().getName());
            planDTO.setFrequency(planDetailDTO.getPlan().getFrequency());
            planDTO.setPlanNumber(planDetailDTO.getPlan().getPlanNumber());
            planDTO.setDescription(planDetailDTO.getPlan().getDescription());
            planDTO.setCreatedBy(planDetailDTO.getPlan().getCreatedBy());
            planDTO.setCreatedAt(planDetailDTO.getPlan().getCreatedAt());
            planDTO.setUpdatedAt(planDetailDTO.getPlan().getUpdatedAt());
            planDTO.setUpdatedBy(planDetailDTO.getPlan().getUpdatedBy());
            planDTO.setStatus(planDetailDTO.getPlan().getStatus());
            planDTO.setPlanType(planDetailDTO.getPlan().getPlanType());
            List<PlanDetailDTO> planDetailDTOS = new ArrayList<>();
            planDTO.setPlanDetails(planDetailDTOS.add(planDetailDTO)?planDetailDTOS:null);
            // lay du lieuj plan result
            List<PlanResultDTO> planResultDTOS = planResultService.findAllByPlanDetailId(planDetailDTO.getId());//  lay du lieu plan result
            for (PlanResultDTO planResultDTO : planResultDTOS) { // lay du lieu plan result detail
            List<PlanResultDetailDTO> planResultDetailDTOS = planResultDetailService.findAllByPlanResultId(planResultDTO.getId()); // lay du lieu plan result detail
            planResultDTO.setPlanResultDetails(planResultDetailDTOS); // set du lieu plan result detail
            }
            List<PlanResultDTO> savePlanResults =  planResultDTOS.stream().filter(planResultDTO -> planResultDTO.getPlanResultDetails().isEmpty()).toList();// loc du lieu plan result khong co plan result detail
            if (savePlanResults.isEmpty()) {// neu co du lieu thi set vao plan detail
            planDetailDTO.setPlanResults(savePlanResults); // set du lieu plan result
            }
            // check trùng
            boolean isDuplicate = false;
            for(PlanDTO existingPlan : plans){
                if(existingPlan.getId().equals(planDTO.getId())){
                    isDuplicate = true;
                    break;
                }
            }
            if(!isDuplicate){
                plans.add(planDTO);
            }
        }
        return plans;
    }
    public PlanDetailDTO get(final Long id) {
        return planDetailRepository.findById(id)
                .map(planDetail -> mapToDTO(planDetail, new PlanDetailDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanDetailDTO planDetailDTO) {
        final PlanDetail planDetail = new PlanDetail();
        mapToEntity(planDetailDTO, planDetail);
        return planDetailRepository.save(planDetail).getId();
    }
    public List<Long> creates(final List<PlanDetailDTO> planDetailDTOS) {
        List<Long> createdIds = new ArrayList<>();
        for (PlanDetailDTO dto : planDetailDTOS) {
            PlanDetail entity;
            if (dto.getId() != null) {
                entity = planDetailRepository.findById(dto.getId()).orElse(new PlanDetail());
            } else {
                entity = new PlanDetail();
            }
            mapToEntity(dto, entity);
            PlanDetail saved = planDetailRepository.save(entity);
            createdIds.add(saved.getId());
        }
        return createdIds;
    }

    public void update(final Long id, final PlanDetailDTO planDetailDTO) {
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planDetailDTO, planDetail);
        planDetailRepository.save(planDetail);
    }

    public void delete(final Long id) {
        List<PlanResult> planResults = planResultRepository.findByPlanDetailId(id);
        planResultRepository.deleteAll(planResults);
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        planDetailRepository.delete(planDetail);
    }

    public PlanDetailDTO mapToDTO(final PlanDetail planDetail, final PlanDetailDTO dto) {
        dto.setId(planDetail.getId());
        dto.setSerial(planDetail.getSerial());
        dto.setEstimatedTime(planDetail.getEstimatedTime());
        dto.setNameDetail(planDetail.getNameDetail());
        dto.setDetail(planDetail.getDetail());
        dto.setNote(planDetail.getNote());
        dto.setCreatedAt(planDetail.getCreatedAt());
        dto.setUpdatedAt(planDetail.getUpdatedAt());
        dto.setCreatedBy(planDetail.getCreatedBy());
        dto.setUpdatedBy(planDetail.getUpdatedBy());
        dto.setManager(planDetail.getManager());
        dto.setStatus(planDetail.getStatus());

        // Sao chép Plan có kiểm soát
        if (planDetail.getPlan() != null) {
            Plan planCopy = new Plan();
            planCopy.setId(planDetail.getPlan().getId());
            planCopy.setName(planDetail.getPlan().getName());
            planCopy.setFrequency(planDetail.getPlan().getFrequency());
            planCopy.setPlanNumber(planDetail.getPlan().getPlanNumber());
            planCopy.setDescription(planDetail.getPlan().getDescription());
            planCopy.setCreatedBy(planDetail.getPlan().getCreatedBy());
            planCopy.setCreatedAt(planDetail.getPlan().getCreatedAt());
            planCopy.setUpdatedAt(planDetail.getPlan().getUpdatedAt());
            planCopy.setUpdatedBy(planDetail.getPlan().getUpdatedBy());
            planCopy.setStatus(planDetail.getPlan().getStatus());
            planCopy.setPlanType(planDetail.getPlan().getPlanType());
            // Xóa các quan hệ con
            planCopy.getPlanType().setPlanTypePlans(null);
            planCopy.setPlanPlanDetails(null);

            dto.setPlan(planCopy);
        } else {
            dto.setPlan(null);
        }

        // Sao chép Device có kiểm soát
        if (planDetail.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(planDetail.getDevice().getId());
            deviceCopy.setCode(planDetail.getDevice().getCode());
            deviceCopy.setName(planDetail.getDevice().getName());
            deviceCopy.setSerialNumber(planDetail.getDevice().getSerialNumber());
            deviceCopy.setUnit(planDetail.getDevice().getUnit());
            deviceCopy.setStatus(planDetail.getDevice().getStatus());
            deviceCopy.setCreatedAt(planDetail.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(planDetail.getDevice().getUpdatedAt());

            // Xóa các quan hệ con
            deviceCopy.setGroup(null);
            deviceCopy.setLine(null);
            deviceCopy.setBranch(null);
            deviceCopy.setTeam(null);
            deviceCopy.setDeviceDeviceParameterUses(null);
            deviceCopy.setDeviceDeviceRelocationHistories(null);
            deviceCopy.setDeviceDeviceSupplyUsages(null);
            deviceCopy.setDevicePlanDetails(null);

            dto.setDevice(deviceCopy);
        } else {
            dto.setDevice(null);
        }

        // Sao chép DeviceGroup có kiểm soát
        if (planDetail.getDeviceGroup() != null) {
            DeviceGroup groupCopy = new DeviceGroup();
            groupCopy.setId(planDetail.getDeviceGroup().getId());
            groupCopy.setCode(planDetail.getDeviceGroup().getCode());
            groupCopy.setName(planDetail.getDeviceGroup().getName());
            groupCopy.setDescription(planDetail.getDeviceGroup().getDescription());
            groupCopy.setCreatedAt(planDetail.getDeviceGroup().getCreatedAt());
            groupCopy.setUpdatedAt(planDetail.getDeviceGroup().getUpdatedAt());
            groupCopy.setCreatedBy(planDetail.getDeviceGroup().getCreatedBy());
            groupCopy.setUpdatedBy(planDetail.getDeviceGroup().getUpdatedBy());
            groupCopy.setStatus(planDetail.getDeviceGroup().getStatus());

            // Xóa các quan hệ con
            groupCopy.setDeviceGroupSampleReports(null);
            groupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);
            groupCopy.setGroupDevices(null);
            groupCopy.setDeviceGroupPlanDetails(null);

            dto.setDeviceGroup(groupCopy);
        } else {
            dto.setDeviceGroup(null);
        }

        if(planDetail.getSampleReport() != null) {
            SampleReport sampleReportCopy = new SampleReport();
            sampleReportCopy.setId(planDetail.getSampleReport().getId());
            sampleReportCopy.setCode(planDetail.getSampleReport().getCode());
            sampleReportCopy.setName(planDetail.getSampleReport().getName());
            sampleReportCopy.setStatus(planDetail.getSampleReport().getStatus());

            // Xóa các quan hệ con
            sampleReportCopy.setDeviceGroup(null);
            sampleReportCopy.setSampleReportKeyMappingDeviceSampleReports(null);
            sampleReportCopy.setSampleReportKeyMappings(null);
            sampleReportCopy.setBranch(null);
            sampleReportCopy.setApprovalWorkflow(null);
        }
        if(planDetail.getPlanResults().isEmpty() || planDetail.getPlanResults() == null){
            dto.setPlanResults(new ArrayList<>());
        } else {
            List<PlanResultDTO> planResultDTOS = new ArrayList<>();
            for(PlanResult planResult : planDetail.getPlanResults()){
                PlanResultDTO planResultDTO = planResultService.mapToDTO(planResult, new PlanResultDTO());
                // Xóa các quan hệ con không cần thiết

                planResultDTO.setPlanDetail(null);
                planResultDTOS.add(planResultDTO);
            }
            dto.setPlanResults(planResultDTOS);
        }
        return dto;
    }

    public List<PlanDetailDTO> getPlanDetailsByPlanId(final Long planId) {
        final List<PlanDetail> planDetails = planDetailRepository.findAllByPlanId(planId);
        return planDetails.stream()
                .map(planDetail -> mapToDTO(planDetail, new PlanDetailDTO()))
                .toList();
    }
    public PlanDetail mapToEntity(final PlanDetailDTO planDetailDTO, final PlanDetail planDetail) {
        planDetail.setSerial(planDetailDTO.getSerial());
        planDetail.setEstimatedTime(planDetailDTO.getEstimatedTime());
        planDetail.setNameDetail(planDetailDTO.getNameDetail());
        planDetail.setDetail(planDetailDTO.getDetail());
        planDetail.setCreatedAt(planDetailDTO.getCreatedAt());
        planDetail.setUpdatedAt(planDetailDTO.getUpdatedAt());
        planDetail.setCreatedBy(planDetailDTO.getCreatedBy());
        planDetail.setUpdatedBy(planDetailDTO.getUpdatedBy());
        planDetail.setManager(planDetailDTO.getManager());
        planDetail.setStatus(planDetailDTO.getStatus());
        final Plan plan = planDetailDTO.getPlan() == null ? null : planRepository.findById(planDetailDTO.getPlan().getId())
                .orElseThrow(() -> new NotFoundException("plan not found"));
        planDetail.setPlan(plan);
        final Device device = planDetailDTO.getDevice() == null ? null : deviceRepository.findById(planDetailDTO.getDevice().getId())
                .orElseThrow(() -> new NotFoundException("device not found"));
        planDetail.setDevice(device);
        final DeviceGroup deviceGroup = planDetailDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(planDetailDTO.getDeviceGroup().getId())
                .orElseThrow(() -> new NotFoundException("deviceGroup not found"));
        planDetail.setDeviceGroup(deviceGroup);

        final SampleReport sampleReport = planDetailDTO.getSampleReport() == null ? null : sampleReportRepository.findById(planDetailDTO.getSampleReport().getId())
                .orElseThrow(() -> new NotFoundException("sampleReport not found"));
        planDetail.setSampleReport(sampleReport);
        return planDetail;
    }

    @EventListener(BeforeDeletePlan.class)
    public void on(final BeforeDeletePlan event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanDetail planPlanDetail = planDetailRepository.findFirstByPlanId(event.getId());
        if (planPlanDetail != null) {
            referencedException.setKey("plan.planDetail.plan.referenced");
            referencedException.addParam(planPlanDetail.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanDetail devicePlanDetail = planDetailRepository.findFirstByDeviceId(event.getId());
        if (devicePlanDetail != null) {
            referencedException.setKey("device.planDetail.device.referenced");
            referencedException.addParam(devicePlanDetail.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteDeviceGroup.class)
    public void on(final BeforeDeleteDeviceGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanDetail deviceGroupPlanDetail = planDetailRepository.findFirstByDeviceGroupId(event.getId());
        if (deviceGroupPlanDetail != null) {
            referencedException.setKey("deviceGroup.planDetail.deviceGroup.referenced");
            referencedException.addParam(deviceGroupPlanDetail.getId());
            throw referencedException;
        }
    }

}
