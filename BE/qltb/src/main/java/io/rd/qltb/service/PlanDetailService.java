package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.events.BeforeDeletePlan;
import io.rd.qltb.model.*;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.ConstantStatusGlobal.DRAFF;
import static io.rd.qltb.config.ConstantStatusGlobal.IN_PROGRESS;


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
    private final ErrorReportRepository errorReportRepository;
    private final ErrorReportService errorReportService;

    public PlanDetailService(final PlanDetailRepository planDetailRepository,
                             final PlanRepository planRepository, final DeviceRepository deviceRepository,
                             final SampleReportRepository sampleReportRepository,
                             final DeviceGroupRepository deviceGroupRepository, ApprovalRepository approvalRepository, ApprovalService approvalService, PlanResultDetailRepository planResultDetailRepository, PlanResultService planResultService, PlanResultDetailService planResultDetailService, PlanResultRepository planResultRepository, ApprovalWorkflowRepository approvalWorkflowRepository, ErrorReportRepository errorReportRepository, ErrorReportService errorReportService) {
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
        this.errorReportRepository = errorReportRepository;
        this.errorReportService = errorReportService;
    }
    public List<PlanDetailDTO> getByPlanId (final Long planId) {
        final List<PlanDetail> planDetails = planDetailRepository.findAllByPlanId(planId);
        return planDetails.stream()
                .map(planDetail -> mapToDTO(planDetail, new PlanDetailDTO()))
                .toList();
    }
    public PlanCheckDTO getPlanCheckDetail(final Long id, String entityType) {
        // Lấy thông tin PlanDetail
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        PlanDetailDTO planDetailDTO = mapToDTO(planDetail, new PlanDetailDTO());
        // Set thêm thông tin liên quan
        if (planDetail.getDeviceGroup() != null) {
        planDetailDTO.setSampleReport(planDetail.getSampleReport());
        planDetailDTO.getSampleReport().setDeviceGroup(null);
        planDetailDTO.getSampleReport().setBranch(null);
        planDetailDTO.getSampleReport().setApprovalWorkflow(null);
        planDetailDTO.getSampleReport().setSampleReportKeyMappingDeviceSampleReports(null);
        planDetailDTO.getSampleReport().setSampleReportKeyMappings(null);
        }
        if (planDetail.getDevice().getBranch()!= null){
        planDetailDTO.getDevice().setBranch(planDetail.getDevice().getBranch());
        planDetailDTO.getDevice().getBranch().getFactory().setFactoryBranches(null);
        planDetailDTO.getDevice().getBranch().setBranchTeams(null);
        planDetailDTO.getDevice().getBranch().setBranchDevices(null);
        planDetailDTO.getDevice().getBranch().setSampleReports(null);
        }
        if (planDetail.getDevice().getLine()!=null){
        planDetailDTO.getDevice().setLine(planDetail.getDevice().getLine());
        planDetailDTO.getDevice().getLine().setTeam(null);
        planDetailDTO.getDevice().getLine().setLineDevices(null);
        }
        if (planDetail.getDevice().getTeam()!=null){
        planDetailDTO.getDevice().setTeam(planDetail.getDevice().getTeam());
        planDetailDTO.getDevice().getTeam().setBranch(null);
        planDetailDTO.getDevice().getTeam().setTeamDevices(null);
        planDetailDTO.getDevice().getTeam().setTeamLines(null);
        }
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
        List<ErrorReportDTO> errorReportDTOS = errorReportRepository.findByPlanDetailId(id).stream().map(item->
                errorReportService.mapToDTO(item,new ErrorReportDTO())).toList();
        planCheckDTO.setErrorReport(errorReportDTOS);
        return planCheckDTO;
    }

    public PlanCheckDTO getPlanCheckDetailByDeviceId(final Long deviceId, String entityType) {
        List<PlanDetail> planDetails = planDetailRepository.findAllByDeviceId(deviceId);
        
        List<PlanDetail> validPlanDetails = planDetails.stream()
            .filter(detail -> detail.getPlan() != null && detail.getPlan().getStatus() != 10)
            .collect(Collectors.toList());

        if (validPlanDetails.isEmpty()) {
            throw new NotFoundException("Thiết bị này không có trong kế hoạch kiểm tra");
        }
        
        PlanDetail latestPlanDetail = validPlanDetails.stream()
            .max(Comparator.comparing(PlanDetail::getId))
            .orElseThrow(NotFoundException::new);

        return getPlanCheckDetail(latestPlanDetail.getId(), entityType);
    }

    public List<PlanDetailDTO> findAll() {
        final List<PlanDetail> planDetails = planDetailRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return planDetails.stream()
                .map(planDetail -> mapToDTO(planDetail, new PlanDetailDTO()))
                .toList();
    }

    public List<PlanDTO> getByDetiveId(final String qrCode) {
        Device device = deviceRepository.findFirstByQrCode(qrCode);
        if (device == null) {
            return Collections.emptyList();
        }

        List<Integer> statuses = Arrays.asList(DRAFF, IN_PROGRESS);

        List<PlanDetailDTO> planDetails = planDetailRepository
                .findAllByDeviceIdAndStatusIn(device.getId(), statuses)
                .stream()
                .map(pd -> mapToDTO(pd, new PlanDetailDTO()))
                .toList();

        Map<Long, PlanDTO> planMap = new LinkedHashMap<>();

        for (PlanDetailDTO planDetailDTO : planDetails) {
            // 1. Kiểm tra NULL và lọc bỏ Plan có status = 10
            if (planDetailDTO.getPlan() == null ||
                    planDetailDTO.getPlan().getId() == null ||
                    planDetailDTO.getPlan().getStatus() == 10) { // Thêm điều kiện lọc status 10 ở đây
                continue;
            }

            // 2. Lấy tất cả PlanResult của PlanDetail
            List<PlanResultDTO> planResultDTOS = planResultService.findAllByPlanDetailId(planDetailDTO.getId());

            // Gắn PlanResultDetails cho từng PlanResult
            for (PlanResultDTO planResultDTO : planResultDTOS) {
                List<PlanResultDetailDTO> planResultDetailDTOS =
                        planResultDetailService.findAllByPlanResultId(planResultDTO.getId());
                planResultDTO.setPlanResultDetails(planResultDetailDTOS);
            }

            // 3. Giữ lại PlanResult có status khác 5
            List<PlanResultDTO> resultsWithoutDetails = planResultDTOS.stream()
                    .filter(r -> r.getStatus() != 5)
                    .toList();

            // Nếu còn kết quả hợp lệ thì set vào planDetail
            if (!resultsWithoutDetails.isEmpty()) {
                planDetailDTO.setPlanResults(resultsWithoutDetails);
            }

            // 4. Gom về PlanDTO
            Long planId = planDetailDTO.getPlan().getId();
            PlanDTO planDTO = planMap.get(planId);
            if (planDTO == null) {
                // Có thể dùng BeanUtils.copyProperties hoặc constructor để code gọn hơn
                planDTO = createPlanDTOFromPlan(planDetailDTO.getPlan());
                planMap.put(planId, planDTO);
            }
            planDTO.getPlanDetails().add(planDetailDTO);
        }

        return new ArrayList<>(planMap.values());
    }

    // Hàm bổ trợ để code sạch hơn (Refactor)
    private PlanDTO createPlanDTOFromPlan(Plan planInner) {
        PlanDTO planDTO = new PlanDTO();
        planDTO.setId(planInner.getId());
        planDTO.setName(planInner.getName());
        planDTO.setFrequency(planInner.getFrequency());
        planDTO.setPlanNumber(planInner.getPlanNumber());
        planDTO.setDescription(planInner.getDescription());
        planDTO.setCreatedBy(planInner.getCreatedBy());
        planDTO.setCreatedAt(planInner.getCreatedAt());
        planDTO.setUpdatedAt(planInner.getUpdatedAt());
        planDTO.setUpdatedBy(planInner.getUpdatedBy());
        planDTO.setStatus(planInner.getStatus());
        planDTO.setPlanType(planInner.getPlanType());
        planDTO.setPlanDetails(new ArrayList<>());
        return planDTO;
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
        dto.setQrCode(planDetail.getQrCode());
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

        if (planDetail.getSampleReport() != null) {
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
        if (planDetail.getPlanResults() == null || planDetail.getPlanResults().isEmpty()) {
            dto.setPlanResults(new ArrayList<>());
        } else {
            List<PlanResultDTO> planResultDTOS = new ArrayList<>();
            for (PlanResult planResult : planDetail.getPlanResults()) {
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
        planDetail.setQrCode(planDetailDTO.getQrCode());
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
