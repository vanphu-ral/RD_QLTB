package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeletePlan;
import io.rd.qltb.events.BeforeDeletePlanType;
import io.rd.qltb.model.*;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.util.*;
import java.util.function.Predicate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanService {
    @PersistenceContext
    private EntityManager entityManager;
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
    private final DeviceGroupService deviceGroupService;
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

    @Transactional
    public Page<PlanDTO> findPlansPaged(Map<String, Object> filters, int page) {
        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(Plan.class);
        var root = cq.from(Plan.class);

        List<Predicate> predicates = new ArrayList<>();
        filters.forEach((key, value) -> {
            if (value != null) {
                switch (key) {
                    case "code", "name", "frequency", "planNumber", "userPerformer", "description", "createdBy", "updatedBy" ->
                            predicates.add((Predicate) cb.like(root.get(key), "%" + value + "%"));
                    case "status" ->
                            predicates.add((Predicate) cb.equal(root.get(key), value));
                    case "planTypeId" ->
                            predicates.add((Predicate) cb.equal(root.get("planType").get("id"), value));
                    case "factoryId" ->
                            predicates.add((Predicate) cb.equal(root.get("factory").get("id"), value));
                    case "branchId" ->
                            predicates.add((Predicate) cb.equal(root.get("branch").get("id"), value));
                    case "approvalWorkflowId" ->
                            predicates.add((Predicate) cb.equal(root.get("approvalWorkflow").get("id"), value));
                    // Thêm các trường khác nếu cần
                }
            }
        });

        cq.where(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        var query = entityManager.createQuery(cq);
        query.setFirstResult(page * 10);
        query.setMaxResults(10);

        List<Plan> plans = query.getResultList();
        List<PlanDTO> dtos = plans.stream()
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .toList();
        for (PlanDTO dto : dtos) {
            List<PlanDetail> details = planDetailRepository.findAllByPlanId(dto.getId());
            dto.setPlanDetails(details.stream()
                    .map(detail -> planDetailService.mapToDTO(detail, new PlanDetailDTO()))
                    .toList());
        }
        // Nếu cần tổng số bản ghi để phân trang, hãy query count riêng
        return new PageImpl<>(dtos, PageRequest.of(page, 10), dtos.size());
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

    public void createPlan2(PlanRequest2 planRequest) {
        Plan plan = mapToEntity(planRequest.getPlan(), new Plan());
        plan = planRepository.save(plan);
        List<PlanDetail> planDetails = new ArrayList<>();
        for (PlanDetailDTO planDetailDTO : planRequest.getPlanDetails()) {
            PlanDetail planDetail = planDetailService.mapToEntity(planDetailDTO, new PlanDetail());
            planDetail.setPlan(plan);
            planDetails.add(planDetail);
        }
    }

    public PlanRequest getPlanDetail(final Long id) {
        PlanRequest planRequest = new PlanRequest();
        Plan plan = planRepository.findById(id).orElse(null);

        if (plan == null) return planRequest;

        Set<PlanDetail> planDetailsSet = plan.getPlanPlanDetails();
        Map<String, PLanDetailRequest> uniquePlanDetails = new HashMap<>();
        Map<Long, DeviceRequest> uniqueDevices = new HashMap<>();

        for (PlanDetail planDetail : planDetailsSet) {
            // Tạo key duy nhất cho PlanDetailRequest dựa trên deviceGroupId và sampleReportId
            Long deviceGroupId = planDetail.getDeviceGroup() != null ? planDetail.getDeviceGroup().getId() : 0L;
            Long sampleReportId = planDetail.getSampleReport() != null ? planDetail.getSampleReport().getId() : 0L;
            String detailKey = deviceGroupId + "-" + sampleReportId;

            if (!uniquePlanDetails.containsKey(detailKey)) {
                PLanDetailRequest detailRequest = new PLanDetailRequest();
                detailRequest.setDeviceGroup(deviceGroupService.mapToDTO(planDetail.getDeviceGroup(), new DeviceGroupDTO()));
                detailRequest.setSampleReport(sampleReportService.mapToDTO(planDetail.getSampleReport(), new SampleReportDTO()));
                uniquePlanDetails.put(detailKey, detailRequest);
            }

            // Tạo key duy nhất cho DeviceRequest dựa trên deviceId
            Long deviceId = planDetail.getDevice() != null ? planDetail.getDevice().getId() : 0L;
            if (!uniqueDevices.containsKey(deviceId)) {
                DeviceRequest deviceRequest = new DeviceRequest();
                deviceRequest.setDevice(deviceService.mapToDTO(planDetail.getDevice(), new DeviceDTO()));
                deviceRequest.setSerialNumber(planDetail.getSerial());
                deviceRequest.setManager(planDetail.getManager());
                deviceRequest.setPlanDetailId(planDetail.getId());
                uniqueDevices.put(deviceId, deviceRequest);
            }
        }

        planRequest.setPlanDetails(new ArrayList<>(uniquePlanDetails.values()));
        planRequest.setDevices(new ArrayList<>(uniqueDevices.values()));

        // Làm sạch các quan hệ để tránh vòng lặp hoặc dữ liệu thừa
        if (plan.getPlanType() != null) plan.getPlanType().setPlanTypePlans(null);
        if (plan.getFactory() != null) plan.getFactory().setFactoryBranches(null);
        if (plan.getBranch() != null) {
            plan.getBranch().setFactory(null);
            plan.getBranch().setBranchDevices(null);
            plan.getBranch().setBranchTeams(null);
            plan.getBranch().setSampleReports(null);
        }
        plan.setPlanPlanDetails(null);
        if (plan.getApprovalWorkflow() != null) {
            plan.getApprovalWorkflow().setWorkflowSampleReports(null);
            plan.getApprovalWorkflow().setWorkflowApprovalGroups(null);
        }

        planRequest.setPlan(plan);
        return planRequest;
    }


    public void createPlanWithDetails(final PlanRequest planRequest, String userName) {
        // check plan
        if (planRequest.getPlan().getId() == null) {
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
                for (DeviceRequest deviceRequest : planRequest.getDevices()) {
                    if (deviceRequest.getDevice().getGroup().getId() == detail.getDeviceGroup().getId()) {
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
        } else {
            System.out.println("Mã kế hoạch đã tồn tại :: "+ planRequest.getPlan().getCode() + " :: " + planRequest.getPlan().getName());
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
            List<PlanDetail> planDetailSend = new ArrayList<>();
            // Gán Plan đã lưu cho từng PlanDetail và lưu chúng

            for (DeviceRequest deviceRequest : planRequest.getDevices()) {
                PlanDetail planDetail = planDetailRepository.findById(deviceRequest.getPlanDetailId()).orElse(null);
                Device deviceSave = deviceRepository.findById(deviceRequest.getDevice().getId())
                        .orElseThrow(() -> new NotFoundException("Device not found"));
                if (planDetail != null) {
                    for (PLanDetailRequest detail : planRequest.getPlanDetails()) {
                        if (deviceSave.getGroup().getId() == detail.getDeviceGroup().getId()) {
                            planDetail.setDeviceGroup(deviceSave.getGroup());
                            planDetail.setSampleReport(sampleReportRepository.findById(detail.getSampleReport().getId())
                                    .orElseThrow(() -> new NotFoundException("SampleReport not found")));
                            planDetail.setDevice(deviceSave);
                            planDetail.setManager(deviceRequest.getManager());
                            planDetail.setSerial(deviceRequest.getSerialNumber());
                            planDetail.setUpdatedAt(java.time.LocalDateTime.now());
                            planDetail.setUpdatedBy(userName);
                            planDetailRepository.save(planDetail);
                        }
                    }
                }
            }

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
    public void deleteByPlanId(final Long planId) {
        List<PlanDetail> plans = planDetailRepository.findAllByPlanId(planId);
        planDetailRepository.deleteAll(plans);
            planRepository.deleteById(planId);
    }


    public List<PlanWithDetailsDTO> findAllWithDetails() {
        List<Plan> plans = planRepository.findAll();

        return plans.stream().map(plan -> {
            PlanWithDetailsDTO dto = new PlanWithDetailsDTO();
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

            if (plan.getPlanType() != null) {
                dto.setPlanTypeName(plan.getPlanType().getName());
            }
            if (plan.getFactory() != null) {
                dto.setFactoryName(plan.getFactory().getName());
            }
            if (plan.getBranch() != null) {
                dto.setBranchName(plan.getBranch().getName());
            }
            if (plan.getApprovalWorkflow() != null) {
                dto.setApprovalWorkflowName(plan.getApprovalWorkflow().getName());
            }

            // Map children (PlanDetail → PlanDetailDTO)
            List<PlanDetailListDTO> details = plan.getPlanPlanDetails().stream().map(detail -> {
                PlanDetailListDTO d = new PlanDetailListDTO();
                d.setId(detail.getId());
                d.setSerial(detail.getSerial());
                d.setManager(detail.getManager());
                d.setStatus(detail.getStatus());
                d.setCreatedBy(detail.getCreatedBy());
                d.setUpdatedBy(detail.getUpdatedBy());
                d.setCreatedAt(detail.getCreatedAt());
                d.setUpdatedAt(detail.getUpdatedAt());

                if (detail.getDevice() != null) {
                    d.setDeviceId(detail.getDevice().getId());
                    d.setDeviceCode(detail.getDevice().getCode());
                    d.setDeviceName(detail.getDevice().getName());
                }
                if (detail.getDeviceGroup() != null) {
                    d.setDeviceGroupId(detail.getDeviceGroup().getId());
                    d.setDeviceGroupCode(detail.getDeviceGroup().getCode());
                    d.setDeviceGroupName(detail.getDeviceGroup().getName());
                }
                if (detail.getSampleReport() != null) {
                    d.setSampleReportId(detail.getSampleReport().getId());
                    d.setSampleReportCode(detail.getSampleReport().getCode());
                    d.setSampleReportName(detail.getSampleReport().getName());
                }

                return d;
            }).toList();

            dto.setDetails(details);
            return dto;
        }).toList();
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

        if (plan.getFactory() != null) {
            Factory factoryCopy = new Factory();
            factoryCopy.setId(plan.getFactory().getId());
            factoryCopy.setCode(plan.getFactory().getCode());
            factoryCopy.setName(plan.getFactory().getName());
            factoryCopy.setStatus(plan.getFactory().getStatus());

            factoryCopy.setFactoryBranches(null);

            dto.setFactory(factoryCopy);
        } else {
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

        if (plan.getApprovalWorkflow() != null) {
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

    public ApplicationEventPublisher getPublisher() {
        return publisher;
    }
}
