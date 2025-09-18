package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeletePlan;
import io.rd.qltb.events.BeforeDeletePlanType;
import io.rd.qltb.model.*;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanTypeRepository planTypeRepository;
    private final FactoryRepository factoryRepository;
    private final BranchRepository branchRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final ApplicationEventPublisher publisher;
    private final PlanDetailRepository planDetailRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final SampleReportRepository sampleReportRepository;
    private final DeviceGroupService  deviceGroupService;
    private final SampleReportService sampleReportService;
    private final DeviceService deviceService;
 private final PlanDetailService planDetailService;
    public PlanService(final PlanRepository planRepository,
                       final PlanTypeRepository planTypeRepository,
                       final FactoryRepository factoryRepository,
                       final BranchRepository branchRepository,
                       final ApprovalWorkflowRepository approvalWorkflowRepository,
                       final ApplicationEventPublisher publisher, PlanDetailRepository planDetailRepository, DeviceRepository deviceRepository, DeviceGroupRepository deviceGroupRepository, SampleReportRepository sampleReportRepository, DeviceGroupService deviceGroupService, SampleReportService sampleReportService, DeviceService deviceService, PlanDetailService planDetailService) {
        this.planRepository = planRepository;
        this.planTypeRepository = planTypeRepository;
        this.factoryRepository = factoryRepository;
        this.branchRepository = branchRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
        this.publisher = publisher;
        this.planDetailRepository = planDetailRepository;
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.sampleReportRepository = sampleReportRepository;
        this.deviceGroupService = deviceGroupService;
        this.sampleReportService = sampleReportService;
        this.deviceService = deviceService;
        this.planDetailService = planDetailService;
    }

    public List<PlanDTO> findAll() {
        final List<Plan> plans = planRepository.findAll(Sort.by("id"));
        return plans.stream()
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .toList();
    }

    public PlanDTO get(final Long id) {
        return planRepository.findById(id)
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanDTO planDTO) {
        final Plan plan = new Plan();
        mapToEntity(planDTO, plan);
        return planRepository.save(plan).getId();
    }
    public PlanRequest getPlanDetail(final Long id) {
        PlanRequest planRequest = new PlanRequest();
         Plan plan = planRepository.findById(id).orElse( null );
        if(plan != null){
            Set<PlanDetail> planDetailsSet = plan.getPlanPlanDetails();
        List<PlanDetail> planDetails = new ArrayList<>(planDetailsSet);
        List<PLanDetailRequest> planDetailRequests = new ArrayList<>();
        List<DeviceRequest> deviceRequests = new ArrayList<>();
            List<PLanDetailRequest> tempPlanDetailRequests = new ArrayList<>();
            List<DeviceRequest> tempDeviceRequests = new ArrayList<>();
            for (PlanDetail planDetail : planDetails) {
                if (planDetailRequests.isEmpty()) {
                    PLanDetailRequest planDetailRequest = new PLanDetailRequest();
                    planDetailRequest.setDeviceGroup(deviceGroupService.mapToDTO(planDetail.getDeviceGroup(), new DeviceGroupDTO()));
                    planDetailRequest.setSampleReport(sampleReportService.mapToDTO(planDetail.getSampleReport(), new SampleReportDTO()));
                    tempPlanDetailRequests.add(planDetailRequest);
                } else {
                    for (PLanDetailRequest pLanDetailRequest : planDetailRequests) {
                        if ((!Objects.equals(pLanDetailRequest.getDeviceGroup().getId(), planDetail.getDeviceGroup().getId()) &&
                                Objects.equals(pLanDetailRequest.getSampleReport().getId(), planDetail.getSampleReport().getId())) || (
                                Objects.equals(pLanDetailRequest.getDeviceGroup().getId(), planDetail.getDeviceGroup().getId()) &&
                                        !Objects.equals(pLanDetailRequest.getSampleReport().getId(), planDetail.getSampleReport().getId())
                        )) {
                            PLanDetailRequest planDetailRequest = new PLanDetailRequest();
                            planDetailRequest.setDeviceGroup(deviceGroupService.mapToDTO(planDetail.getDeviceGroup(), new DeviceGroupDTO()));
                            planDetailRequest.setSampleReport(sampleReportService.mapToDTO(planDetail.getSampleReport(), new SampleReportDTO()));
                            tempPlanDetailRequests.add(planDetailRequest);
                        }
                    }
                }
                if (deviceRequests.isEmpty()) {
                    DeviceRequest deviceRequest = new DeviceRequest();
                    deviceRequest.setDevice(deviceService.mapToDTO(planDetail.getDevice(), new DeviceDTO()));
                    deviceRequest.setSerialNumber(planDetail.getSerial());
                    deviceRequest.setManager(planDetail.getManager());
                    tempDeviceRequests.add(deviceRequest);
                } else {
                    for (DeviceRequest deviceRequest : deviceRequests) {
                        if (!Objects.equals(deviceRequest.getDevice().getId(), planDetail.getDevice().getId())) {
                            DeviceRequest newDeviceRequest = new DeviceRequest();
                            newDeviceRequest.setDevice(deviceService.mapToDTO(planDetail.getDevice(), new DeviceDTO()));
                            newDeviceRequest.setSerialNumber(planDetail.getSerial());
                            newDeviceRequest.setManager(planDetail.getManager());
                            tempDeviceRequests.add(newDeviceRequest);
                        }
                    }
                }
            }
            // Thêm các phần tử từ danh sách tạm thời vào danh sách chính
            planDetailRequests.addAll(tempPlanDetailRequests);
            deviceRequests.addAll(tempDeviceRequests);
            planRequest.setDevices(deviceRequests);
            planRequest.setPlanDetails(planDetailRequests);
    }
//         planDetails = null;
        plan.setPlanType(null);
        plan.setFactory(null);
        plan.setBranch(null);
        plan.setPlanPlanDetails(null);
        plan.setApprovalWorkflow(null);
        planRequest.setPlan(plan);
        return planRequest;
    }
    public void createPlanWithDetails(final PlanRequest planRequest,String userName) {
        // check plan
        if(planRequest.getPlan().getId()==null){
        // Lưu Plan trước
        Plan plan = planRequest.getPlan();
        plan.setCreatedBy(userName);
        plan.setCreatedAt(java.time.LocalDateTime.now());
        plan.setUpdatedAt(java.time.LocalDateTime.now());
        plan.setApprovalWorkflow(planRequest.getPlan().getApprovalWorkflow());
        plan.setBranch(planRequest.getPlan().getBranch());
        plan.setFactory(planRequest.getPlan().getFactory());
        plan.setCode(planRequest.getPlan().getCode());
        plan.setName(planRequest.getPlan().getName());
        plan.setFrequency(planRequest.getPlan().getFrequency());
        plan.setPlanNumber(planRequest.getPlan().getPlanNumber());
        plan.setUserPerformer(planRequest.getPlan().getUserPerformer());
        plan.setDescription(planRequest.getPlan().getDescription());
        plan.setStatus(planRequest.getPlan().getStatus());
        plan.setUpdatedBy(null);
        plan = planRepository.save(plan);
        List<PlanDetail> planDetailSend = new ArrayList<>();
        // Gán Plan đã lưu cho từng PlanDetail và lưu chúng

        for (PLanDetailRequest detail : planRequest.getPlanDetails()) {
            for(DeviceRequest deviceRequest: planRequest.getDevices()){
                if(deviceRequest.getDevice().getGroup().getId() == detail.getDeviceGroup().getId()){
                    PlanDetail planDetail = new PlanDetail();
                    planDetail.setPlan(plan);
                    planDetail.setCreatedAt(java.time.LocalDateTime.now());
                    planDetail.setUpdatedAt(java.time.LocalDateTime.now());
                    planDetail.setCreatedBy(userName);
                    planDetail.setUpdatedBy(null);
                    planDetail.setStatus(1);
                    planDetail.setManager(deviceRequest.getManager());
                    planDetail.setSerial(deviceRequest.getSerialNumber());
                    Device device = deviceRepository.findById(deviceRequest.getDevice().getId())
                            .orElseThrow(() -> new NotFoundException("Device not found"));
                    planDetail.setDevice(device);
                    DeviceGroup deviceGroup = deviceGroupRepository.findById(detail.getDeviceGroup().getId())
                            .orElseThrow(() -> new NotFoundException("DeviceGroup not found"));
                    planDetail.setDeviceGroup(deviceGroup);
                    SampleReport sampleReport = sampleReportRepository.findById(detail.getSampleReport().getId())
                            .orElseThrow(() -> new NotFoundException("SampleReport not found"));
                    planDetail.setSampleReport(sampleReport);
                    planDetailSend.add(planDetailRepository.save(planDetail));
                }
            }

        }
        }else{
            System.out.println("Mã kế hoạch đã tồn tại");
            // Lưu Plan trước
            Plan plan = planRepository.findById(planRequest.getPlan().getId()).orElseThrow();
            plan.setUpdatedAt(java.time.LocalDateTime.now());
            plan.setApprovalWorkflow(planRequest.getPlan().getApprovalWorkflow());
            plan.setBranch(planRequest.getPlan().getBranch());
            plan.setFactory(planRequest.getPlan().getFactory());
            plan.setCode(planRequest.getPlan().getCode());
            plan.setName(planRequest.getPlan().getName());
            plan.setFrequency(planRequest.getPlan().getFrequency());
            plan.setPlanNumber(planRequest.getPlan().getPlanNumber());
            plan.setUserPerformer(planRequest.getPlan().getUserPerformer());
            plan.setDescription(planRequest.getPlan().getDescription());
            plan.setStatus(planRequest.getPlan().getStatus());
            plan.setUpdatedBy(userName);
             planRepository.save(plan);
//            List<PlanDetail> planDetailSend = new ArrayList<>();
//            // Gán Plan đã lưu cho từng PlanDetail và lưu chúng
//
//            for (PLanDetailRequest detail : planRequest.getPlanDetails()) {
//                for(DeviceRequest deviceRequest: planRequest.getDevices()){
//                    if(deviceRequest.getDevice().getGroup().getId() == detail.getDeviceGroup().getId()){
//                        PlanDetail planDetail = new PlanDetail();
//                        planDetail.setPlan(plan);
//                        planDetail.setCreatedAt(java.time.LocalDateTime.now());
//                        planDetail.setUpdatedAt(java.time.LocalDateTime.now());
//                        planDetail.setCreatedBy(userName);
//                        planDetail.setUpdatedBy(null);
//                        planDetail.setStatus(1);
//                        planDetail.setManager(deviceRequest.getManager());
//                        planDetail.setSerial(deviceRequest.getSerialNumber());
//                        Device device = deviceRepository.findById(deviceRequest.getDevice().getId())
//                                .orElseThrow(() -> new NotFoundException("Device not found"));
//                        planDetail.setDevice(device);
//                        DeviceGroup deviceGroup = deviceGroupRepository.findById(detail.getDeviceGroup().getId())
//                                .orElseThrow(() -> new NotFoundException("DeviceGroup not found"));
//                        planDetail.setDeviceGroup(deviceGroup);
//                        SampleReport sampleReport = sampleReportRepository.findById(detail.getSampleReport().getId())
//                                .orElseThrow(() -> new NotFoundException("SampleReport not found"));
//                        planDetail.setSampleReport(sampleReport);
//                        planDetailSend.add(planDetailRepository.save(planDetail));
//                    }
//                }
//
//            }
        }
    }
    public void update(final Long id, final PlanDTO planDTO) {
        final Plan plan = planRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planDTO, plan);
        planRepository.save(plan);
    }

    public void delete(final Long id) {
        final Plan plan = planRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePlan(id));
        planRepository.delete(plan);
    }

    private PlanDTO mapToDTO(final Plan plan, final PlanDTO dto) {
        dto.setId(plan.getId());
        dto.setCode(plan.getCode());
        dto.setName(plan.getName());
        dto.setFrequency(plan.getFrequency());
        dto.setPlanNumber(plan.getPlanNumber());
        dto.setUserPerformer(plan.getUserPerformer());
        dto.setDescription(plan.getDescription());
        dto.setCreatedBy(plan.getCreatedBy());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        dto.setUpdatedBy(plan.getUpdatedBy());
        dto.setStatus(plan.getStatus());

        // Sao chép PlanType có kiểm soát
        if (plan.getPlanType() != null) {
            PlanType planTypeCopy = new PlanType();
            planTypeCopy.setId(plan.getPlanType().getId());
            planTypeCopy.setCode(plan.getPlanType().getCode());
            planTypeCopy.setName(plan.getPlanType().getName());
            planTypeCopy.setCreatedAt(plan.getPlanType().getCreatedAt());
            planTypeCopy.setUpdatedAt(plan.getPlanType().getUpdatedAt());
            planTypeCopy.setCreatedBy(plan.getPlanType().getCreatedBy());
            planTypeCopy.setUpdatedBy(plan.getPlanType().getUpdatedBy());
            planTypeCopy.setStatus(plan.getPlanType().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            planTypeCopy.setPlanTypePlans(null);

            dto.setPlanType(planTypeCopy);
        } else {
            dto.setPlanType(null);
        }

        if(plan.getFactory() != null) {
            Factory factoryCopy = new Factory();
            factoryCopy.setId(plan.getFactory().getId());
            factoryCopy.setCode(plan.getFactory().getCode());
            factoryCopy.setName(plan.getFactory().getName());
            factoryCopy.setStatus(plan.getFactory().getStatus());

            factoryCopy.setFactoryBranches(null);

            dto.setFactory(factoryCopy);
        }else {
            dto.setFactory(null);
        }

        if (plan.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(plan.getBranch().getId());
            branchCopy.setCode(plan.getBranch().getCode());
            branchCopy.setName(plan.getBranch().getName());
            branchCopy.setDescription(plan.getBranch().getDescription());
            branchCopy.setCreatedAt(plan.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(plan.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(plan.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(plan.getBranch().getUpdatedBy());
            branchCopy.setStatus(plan.getBranch().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            branchCopy.setFactory(null);

            dto.setBranch(branchCopy);
        } else {
            dto.setBranch(null);
        }

        if(plan.getApprovalWorkflow() != null) {
            ApprovalWorkflow approvalWorkflowCopy = new ApprovalWorkflow();
            approvalWorkflowCopy.setId(plan.getApprovalWorkflow().getId());
            approvalWorkflowCopy.setCode(plan.getApprovalWorkflow().getCode());
            approvalWorkflowCopy.setName(plan.getApprovalWorkflow().getName());
            approvalWorkflowCopy.setStatus(plan.getApprovalWorkflow().getStatus());

            approvalWorkflowCopy.setWorkflowApprovalGroups(null);

            dto.setApprovalWorkflow(approvalWorkflowCopy);
        } else {
            dto.setApprovalWorkflow(null);
        }

        return dto;
    }


    private Plan mapToEntity(final PlanDTO planDTO, final Plan plan) {
        plan.setCode(planDTO.getCode());
        plan.setName(planDTO.getName());
        plan.setFrequency(planDTO.getFrequency());
        plan.setPlanNumber(planDTO.getPlanNumber());
        plan.setUserPerformer(planDTO.getUserPerformer());
        plan.setDescription(planDTO.getDescription());
        plan.setCreatedBy(planDTO.getCreatedBy());
        plan.setCreatedAt(planDTO.getCreatedAt());
        plan.setUpdatedAt(planDTO.getUpdatedAt());
        plan.setUpdatedBy(planDTO.getUpdatedBy());
        plan.setStatus(planDTO.getStatus());
        final PlanType planType = planDTO.getPlanType() == null ? null : planTypeRepository.findById(planDTO.getPlanType().getId())
                .orElseThrow(() -> new NotFoundException("planType not found"));
        plan.setPlanType(planType);

        final Factory factory = planDTO.getFactory() == null ? null : factoryRepository.findById(planDTO.getFactory().getId())
                .orElseThrow(() -> new NotFoundException("factory not found"));
        plan.setFactory(factory);

        final Branch branch = planDTO.getBranch() == null ? null : branchRepository.findById(planDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        plan.setBranch(branch);

        final ApprovalWorkflow approvalWorkflow = planDTO.getApprovalWorkflow() == null ? null :
                approvalWorkflowRepository.findById(planDTO.getApprovalWorkflow().getId())
                        .orElseThrow(() -> new NotFoundException("approvalWorkflow not found"));
        plan.setApprovalWorkflow(approvalWorkflow);

        return plan;
    }

    @EventListener(BeforeDeletePlanType.class)
    public void on(final BeforeDeletePlanType event) {
        final ReferencedException referencedException = new ReferencedException();
        final Plan planTypePlan = planRepository.findFirstByPlanTypeId(event.getId());
        if (planTypePlan != null) {
            referencedException.setKey("planType.plan.planType.referenced");
            referencedException.addParam(planTypePlan.getId());
            throw referencedException;
        }
    }

}
