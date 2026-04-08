package io.rd.qltb.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeletePlan;
import io.rd.qltb.events.BeforeDeletePlanType;
import io.rd.qltb.model.*;
import io.rd.qltb.model.response.PlanSaveDetailLog;
import io.rd.qltb.model.response.PlanUpdateResponse;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.*;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.ConstantStatusGlobal.DELETED;


@Service
public class PlanService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final PlanRepository planRepository;
    private final PlanTypeRepository planTypeRepository;
    private final FactoryRepository factoryRepository;
    private final BranchRepository branchRepository;
    private final TeamRepository teamRepository;
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
    private final DetailLogService detailLogService;
    private final DetailLogRepository detailLogRepository;
    private final PlanResultService planResultService;
    private final PlanResultRepository planResultRepository;
    private final PlanResultDetailRepository planResultDetailRepository;
    private final KeyMappingRepository keyMappingRepository;
    private final KeyMappingService keyMappingService;
    private final ApprovalGroupUserRepository approvalGroupUserRepository;
    private final ApprovalRepository approvalRepository;
    private final ApprovalGroupRepository approvalGroupRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public PlanService(EntityManager entityManager, final PlanRepository planRepository,
                       final PlanTypeRepository planTypeRepository,
                       final FactoryRepository factoryRepository,
                       final BranchRepository branchRepository,
                       final TeamRepository teamRepository,
                       final ApprovalWorkflowRepository approvalWorkflowRepository,
                       final ApplicationEventPublisher publisher, PlanDetailRepository planDetailRepository, DeviceRepository deviceRepository, DeviceGroupRepository deviceGroupRepository, SampleReportRepository sampleReportRepository, DeviceGroupService deviceGroupService, SampleReportService sampleReportService, DeviceService deviceService, PlanDetailService planDetailService, DetailLogService detailLogService, DetailLogRepository detailLogRepository, PlanResultService planResultService, PlanResultRepository planResultRepository, PlanResultDetailRepository planResultDetailRepository, KeyMappingRepository keyMappingRepository, KeyMappingService keyMappingService, ApprovalGroupUserRepository approvalGroupUserRepository, ApprovalRepository approvalRepository, ApprovalGroupRepository approvalGroupRepository, JdbcTemplate jdbcTemplate) {
        this.entityManager = entityManager;
        this.planRepository = planRepository;
        this.planTypeRepository = planTypeRepository;
        this.factoryRepository = factoryRepository;
        this.branchRepository = branchRepository;
        this.teamRepository = teamRepository;
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
        this.detailLogService = detailLogService;
        this.detailLogRepository = detailLogRepository;
        this.planResultService = planResultService;
        this.planResultRepository = planResultRepository;
        this.planResultDetailRepository = planResultDetailRepository;
        this.keyMappingRepository = keyMappingRepository;
        this.keyMappingService = keyMappingService;
        this.approvalGroupUserRepository = approvalGroupUserRepository;
        this.approvalRepository = approvalRepository;
        this.approvalGroupRepository = approvalGroupRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public Page<PlanDTO> findPlansPaged(Map<String, Object> filters, int page) {
        int pageSize = 10;
        var cb = entityManager.getCriteriaBuilder();

        // 1. Khởi tạo Query lấy dữ liệu
        var cq = cb.createQuery(Plan.class);
        var root = cq.from(Plan.class);

        // 2. Khởi tạo Query đếm tổng
        var countQuery = cb.createQuery(Long.class);
        var countRoot = countQuery.from(Plan.class);

        // 3. Xây dựng Predicates đồng nhất
        Predicate[] dataPredicates = buildPlanPredicates(filters, cb, root);
        Predicate[] countPredicates = buildPlanPredicates(filters, cb, countRoot);

        // 4. Thực thi truy vấn lấy dữ liệu trang
        cq.where(dataPredicates);
        cq.orderBy(cb.desc(root.get("id"))); // Sắp xếp mới nhất lên đầu

        var query = entityManager.createQuery(cq);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        List<PlanDTO> dtos = query.getResultList().stream()
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .toList();

        // 5. Thực thi truy vấn đếm tổng số bản ghi (metadata phân trang)
        countQuery.select(cb.count(countRoot)).where(countPredicates);
        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(dtos, PageRequest.of(page, pageSize), totalRecords);
    }

    /**
     * Hàm xây dựng bộ lọc Predicate cho Plan
     */
    private Predicate[] buildPlanPredicates(Map<String, Object> filters, CriteriaBuilder cb, Root<Plan> root) {
        List<Predicate> predicates = new ArrayList<>();

        // Danh sách các thực thể quan hệ có trong PlanDTO
        List<String> relationKeys = List.of("planType", "factory", "branch", "team", "approvalWorkflow");

        filters.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                Path<?> path;

                // Xử lý Lọc theo quan hệ: Nếu key là "factory", sẽ join Factory và lọc theo "name"
                if (relationKeys.contains(key)) {
                    path = root.join(key, JoinType.LEFT).get("name");
                }
                // Xử lý Lọc sâu (Nested): Nếu key là "factory.code"
                else if (key.contains(".")) {
                    String[] parts = key.split("\\.");
                    Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                    path = join.get(parts[1]);
                }
                // Xử lý các trường trực tiếp trong bảng Plan
                else {
                    path = root.get(key);
                }

                // PHÂN LOẠI KIỂU DỮ LIỆU ĐỂ TẠO ĐIỀU KIỆN LỌC (Predicate)
                if (path.getJavaType().equals(LocalDateTime.class)) {
                    String v = value.toString();
                    LocalDateTime startOfDay = (v.length() == 10)
                            ? LocalDate.parse(v).atStartOfDay()
                            : LocalDateTime.parse(v);
                    LocalDateTime endOfDay = startOfDay.toLocalDate().atTime(LocalTime.MAX);
                    predicates.add(cb.between((Expression<LocalDateTime>) path, startOfDay, endOfDay));
                }
                else if (path.getJavaType().equals(Integer.class) || path.getJavaType().equals(Long.class)) {
                    // Nếu là số (ví dụ status), dùng so sánh bằng thay vì LIKE
                    predicates.add(cb.equal(path, value));
                }
                else {
                    // Mặc định cho String: Tìm kiếm LIKE không phân biệt hoa thường
                    predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                }
            }
        });

        // Luôn lọc bỏ các bản ghi đã xóa ảo (DELETED = 10)
        predicates.add(cb.notEqual(root.get("status"), 10));

        return predicates.toArray(new Predicate[0]);
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
            entityManager.detach(plan);
            if (!uniqueDevices.containsKey(deviceId)) {
                Integer count = planResultDetailRepository.countByDeviceIdAndPlanId(deviceId, plan.getId()) > 0 ? 1 : 0;
                DeviceRequest deviceRequest = new DeviceRequest();
                deviceRequest.setDevice(deviceService.mapToDTO(planDetail.getDevice(), new DeviceDTO()));
                deviceRequest.getDevice().setIsHadDataPlanReport(count);
                deviceRequest.setQrCode(planDetail.getQrCode());
                deviceRequest.setManager(planDetail.getManager());
                deviceRequest.setEstimatedTime(planDetail.getEstimatedTime());
                deviceRequest.setNameDetail(planDetail.getNameDetail());
                deviceRequest.setNote(planDetail.getNote());
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
        if( plan.getTeam() != null) {
            plan.getTeam().setBranch(null);
            plan.getTeam().setTeamDevices(null);
            plan.getTeam().setTeamLines(null);
        }
        plan.setPlanPlanDetails(null);
        if (plan.getApprovalWorkflow() != null) {
            plan.getApprovalWorkflow().setWorkflowSampleReports(null);
            plan.getApprovalWorkflow().setWorkflowApprovalGroups(null);
        }

        planRequest.setPlan(plan);
        return planRequest;
    }
    public PlanSaveDetailLog getPlanDetail2(final Long id) {
        PlanSaveDetailLog planRequest = new PlanSaveDetailLog();
        PlanDTO plan = get(id);
        if (plan == null) return planRequest;

        List<PlanDetailDTO> planDetailsSet = plan.getPlanDetails();
        Map<String, PLanDetailRequest> uniquePlanDetails = new HashMap<>();
        Map<Long, DeviceRequest> uniqueDevices = new HashMap<>();

        for (PlanDetailDTO planDetail : planDetailsSet) {
            // Tạo key duy nhất cho PlanDetailRequest dựa trên deviceGroupId và sampleReportId
            Long deviceGroupId = planDetail.getDeviceGroup() != null ? planDetail.getDeviceGroup().getId() : 0L;
            Long sampleReportId = planDetail.getSampleReport() != null ? planDetail.getSampleReport().getId() : 0L;
            String detailKey = deviceGroupId + "-" + sampleReportId;
            PlanDetail planDetailDTO = planDetailRepository.findById(planDetail.getId()).orElse(null);
            if (!uniquePlanDetails.containsKey(detailKey)) {
                PLanDetailRequest detailRequest = new PLanDetailRequest();
                detailRequest.setDeviceGroup(deviceGroupService.mapToDTO2(planDetailDTO.getDeviceGroup(), new DeviceGroupDTO()));
                detailRequest.setSampleReport(sampleReportService.mapToDTO(planDetailDTO.getSampleReport(), new SampleReportDTO()));
                uniquePlanDetails.put(detailKey, detailRequest);
            }

            // Tạo key duy nhất cho DeviceRequest dựa trên deviceId
            Long deviceId = planDetail.getDevice() != null ? planDetail.getDevice().getId() : 0L;
            if (!uniqueDevices.containsKey(deviceId)) {
                Integer count = planResultDetailRepository.countByDeviceIdAndPlanId(deviceId, plan.getId()) > 0 ? 1 : 0;
                DeviceRequest deviceRequest = new DeviceRequest();
                deviceRequest.setDevice(deviceService.mapToDTO(planDetail.getDevice(), new DeviceDTO()));
                deviceRequest.getDevice().setIsHadDataPlanReport(count);
                deviceRequest.setQrCode(planDetail.getQrCode());
                deviceRequest.setManager(planDetail.getManager());
                deviceRequest.setEstimatedTime(planDetail.getEstimatedTime());
                deviceRequest.setNameDetail(planDetail.getNameDetail());
                deviceRequest.setNote(planDetail.getNote());
                deviceRequest.setPlanDetailId(planDetail.getId());
                uniqueDevices.put(deviceId, deviceRequest);
            }
        }

        planRequest.setPlanDetails(new ArrayList<>(uniquePlanDetails.values()));
        planRequest.setDevices(new ArrayList<>(uniqueDevices.values()));

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
            plan.setTeam(planRequest.getPlan().getTeam() == null ? planRequest.getPlan().getTeam() : null);
            plan.setName(planRequest.getPlan().getName());
            plan.setFrequency(planRequest.getPlan().getFrequency());
            plan.setPlanNumber(planRequest.getPlan().getPlanNumber());
            plan.setUserPerformer(planRequest.getPlan().getUserPerformer());
            plan.setDescription(planRequest.getPlan().getDescription());
            plan.setStatus(planRequest.getPlan().getStatus());
            plan.setFromDate(planRequest.getPlan().getFromDate());
            plan.setToDate(planRequest.getPlan().getToDate());
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
                        planDetail.setEstimatedTime(deviceRequest.getEstimatedTime());
                        planDetail.setNameDetail(deviceRequest.getNameDetail());
                        planDetail.setNote(deviceRequest.getNote());
                        planDetail.setManager(deviceRequest.getManager());
                        planDetail.setQrCode(deviceRequest.getQrCode());
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
                        // Duyệt từng ngày trong tháng
                        if (planRequest.getPlan().getPlanType().getCode().equals("DAILYCHECK")) {
                            // tạo plan Result cho tháng hiện tại
                            // Lấy tháng hiện tại
                            YearMonth currentMonth = YearMonth.now();
                            Duration duration = Duration.between(plan.getFromDate(), plan.getToDate());
                            Integer startDay = plan.getFromDate().getDayOfMonth();
                            // Duyệt từng ngày trong tháng
                            for (int day = 1; day <= duration.toDays(); day++) {
                                LocalDate date = currentMonth.atDay(startDay);
                                // Trả về LocalDateTime lúc 00:00 của ngày đó
                                LocalDateTime dateTime = date.atTime(17, 00, 00);
                                PlanResult planResult = new PlanResult();
                                planResult.setPlanDetail(planDetail);
                                planResult.setCreatedAt(java.time.LocalDateTime.now());
                                planResult.setUpdatedAt(java.time.LocalDateTime.now());
                                planResult.setCreatedBy(userName);
                                planResult.setUpdatedBy(null);
                                planResult.setStatus(1);
                                planResult.setStatusRepair("1");
                                planResult.setNote("");
                                planResult.setDateTest(dateTime);
                                planResult.setUserTest(deviceRequest.getDevice().getUserManager());
                                planResultService.create(planResultService.mapToDTO(planResult, new PlanResultDTO()));
                                startDay++;
                            }
                        }
                    }
                }

            }
        } else {
            System.out.println("Mã kế hoạch đã tồn tại :: " + planRequest.getPlan().getCode() + " :: " + planRequest.getPlan().getName());
            Plan plan = planRepository.findById(planRequest.getPlan().getId()).orElseThrow();
            try {
                // Khởi tạo ObjectMapper với hỗ trợ Java 8 Date/Time
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                PlanDTO planDTO = mapToDTO(plan, new PlanDTO());
                // Convert Plan sang JSON
                String planJson = mapper.writeValueAsString(getPlanDetail(planRequest.getPlan().getId()));

                // Tạo DetailLog
                Integer countLog = detailLogRepository.countByEntityTypeAndEntityId("plans", planRequest.getPlan().getId());
                DetailLogDTO detailLog = new DetailLogDTO();
                detailLog.setEntityType("plans");
                detailLog.setEntityId(planRequest.getPlan().getId());
                detailLog.setDetail(planJson);
                detailLog.setVersion(String.valueOf(countLog + 1));
                detailLog.setCreatedAt(java.time.LocalDateTime.now());
                detailLog.setLoggedAt(java.time.LocalDateTime.now());
                detailLog.setCreatedBy(userName);
                detailLog.setStatus(1);
                detailLogService.create(detailLog);

                // Cập nhật Plan
                Plan planRequestData = planRequest.getPlan();
                plan.setUpdatedAt(java.time.LocalDateTime.now());
                plan.setApprovalWorkflow(planRequestData.getApprovalWorkflow());
                plan.setBranch(planRequestData.getBranch());
                plan.setFactory(planRequestData.getFactory());
                plan.setTeam(planRequestData.getTeam());
                plan.setCode(planRequestData.getCode());
                plan.setName(planRequestData.getName());
                plan.setFrequency(planRequestData.getFrequency());
                plan.setPlanNumber(planRequestData.getPlanNumber());
                plan.setUserPerformer(planRequestData.getUserPerformer());
                plan.setDescription(planRequestData.getDescription());
                plan.setStatus(planRequestData.getStatus());
                plan.setFromDate(planRequestData.getFromDate());
                plan.setToDate(planRequestData.getToDate());
                plan.setUpdatedBy(userName);
                planRepository.save(plan);

                // Cập nhật PlanDetail
                for (DeviceRequest deviceRequest : planRequest.getDevices()) {
                    if (deviceRequest.getPlanDetailId() == null) {
                        for (PLanDetailRequest detail : planRequest.getPlanDetails()) {
                            if (deviceRequest.getDevice().getGroup().getId() == detail.getDeviceGroup().getId()) {
                                PlanDetail planDetail = new PlanDetail();
                                planDetail.setPlan(plan);
                                planDetail.setCreatedAt(java.time.LocalDateTime.now());
                                planDetail.setUpdatedAt(java.time.LocalDateTime.now());
                                planDetail.setCreatedBy(userName);
                                planDetail.setUpdatedBy(null);
                                planDetail.setStatus(1);
                                planDetail.setEstimatedTime(deviceRequest.getEstimatedTime());
                                planDetail.setNameDetail(deviceRequest.getNameDetail());
                                planDetail.setNote(deviceRequest.getNote());
                                planDetail.setManager(deviceRequest.getManager());
                                planDetail.setQrCode(deviceRequest.getQrCode());
                                Device device = deviceRepository.findById(deviceRequest.getDevice().getId())
                                        .orElseThrow(() -> new NotFoundException("Device not found"));
                                planDetail.setDevice(device);
                                DeviceGroup deviceGroup = deviceGroupRepository.findById(detail.getDeviceGroup().getId())
                                        .orElseThrow(() -> new NotFoundException("DeviceGroup not found"));
                                planDetail.setDeviceGroup(deviceGroup);
                                SampleReport sampleReport = sampleReportRepository.findById(detail.getSampleReport().getId())
                                        .orElseThrow(() -> new NotFoundException("SampleReport not found"));
                                planDetail.setSampleReport(sampleReport);
                                planDetailRepository.save(planDetail);

                                if (planRequestData.getPlanType().getCode().equals("DAILYCHECK")) {
                                    // tạo plan Result cho tháng hiện tại
                                    // Lấy tháng hiện tại
                                    YearMonth currentMonth = YearMonth.now();
                                    Duration duration = Duration.between(plan.getFromDate(), plan.getToDate());
                                    Integer startDay = plan.getFromDate().getDayOfMonth();
                                    // Duyệt từng ngày trong tháng
                                    for (int day = 1; day <= duration.toDays(); day++) {
                                        LocalDate date = currentMonth.atDay(startDay);
                                        // Trả về LocalDateTime lúc 00:00 của ngày đó
                                        LocalDateTime dateTime = date.atTime(17, 00, 00);
                                        PlanResult planResult = new PlanResult();
                                        planResult.setPlanDetail(planDetail);
                                        planResult.setCreatedAt(java.time.LocalDateTime.now());
                                        planResult.setUpdatedAt(java.time.LocalDateTime.now());
                                        planResult.setCreatedBy(userName);
                                        planResult.setUpdatedBy(null);
                                        planResult.setStatus(1);
                                        planResult.setStatusRepair("1");
                                        planResult.setNote("");
                                        planResult.setDateTest(dateTime);
                                        planResult.setUserTest(deviceRequest.getDevice().getUserManager());
                                        planResultService.create(planResultService.mapToDTO(planResult, new PlanResultDTO()));
                                        startDay++;
                                    }
                                }
                            }
                        }
                    } else {
                        PlanDetail planDetail = planDetailRepository.findById(deviceRequest.getPlanDetailId()).orElse(null);
                        if (planDetail == null) continue;

                        Device deviceSave = deviceRepository.findById(deviceRequest.getDevice().getId())
                                .orElseThrow(() -> new NotFoundException("Device not found"));

                        for (PLanDetailRequest detail : planRequest.getPlanDetails()) {
                            if (deviceSave.getGroup().getId().equals(detail.getDeviceGroup().getId())) {
                                planDetail.setDeviceGroup(deviceSave.getGroup());
                                planDetail.setSampleReport(sampleReportRepository.findById(detail.getSampleReport().getId())
                                        .orElseThrow(() -> new NotFoundException("SampleReport not found")));
                                planDetail.setDevice(deviceSave);
                                planDetail.setEstimatedTime(deviceRequest.getEstimatedTime());
                                planDetail.setNameDetail(deviceRequest.getNameDetail());
                                planDetail.setNote(deviceRequest.getNote());
                                planDetail.setManager(deviceRequest.getManager());
                                planDetail.setQrCode(deviceRequest.getQrCode());
                                planDetail.setUpdatedAt(java.time.LocalDateTime.now());
                                planDetail.setUpdatedBy(userName);
                                planDetailRepository.save(planDetail);
                            }
                        }
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException("Error while processing Plan update", e);
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
//        publisher.publishEvent(new BeforeDeletePlan(id));
        plan.setStatus(DELETED);// đánh dấu đã xóa
        planRepository.save(plan);
    }

    public void deleteByPlanId(final Long planId) {
        List<PlanDetail> plans = planDetailRepository.findAllByPlanId(planId);
        planDetailRepository.deleteAll(plans);
        planRepository.deleteById(planId);
    }


    public List<PlanWithDetailsDTO> findAllWithDetails() {
        List<Plan> plans = planRepository.findByStatusNot(0);

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
            dto.setFromDate(plan.getFromDate());
            dto.setToDate(plan.getToDate());
            dto.setStatus(plan.getStatus());

            if (plan.getPlanType() != null) {
                dto.setPlanTypeName(plan.getPlanType().getName());
                dto.setPlanTypeCode(plan.getPlanType().getCode());
            }
            if (plan.getFactory() != null) {
                dto.setFactoryName(plan.getFactory().getName());
            }
            if (plan.getBranch() != null) {
                dto.setBranchName(plan.getBranch().getName());
            }
//            if (plan.getApprovalWorkflow() != null) {
//                dto.setApprovalWorkflowName(plan.getApprovalWorkflow().getName());
//            }
            if (plan.getApprovalWorkflow() != null) {
                dto.setApprovalWorkflowName(plan.getApprovalWorkflow().getName());
                dto.setApprovalWorkflowId(plan.getApprovalWorkflow().getId());
                // --- thêm phần gán ApprovalWorkflowDTO ---
                ApprovalWorkflowDTO aw = new ApprovalWorkflowDTO();
                aw.setId(plan.getApprovalWorkflow().getId());
                aw.setCode(plan.getApprovalWorkflow().getCode());
                aw.setName(plan.getApprovalWorkflow().getName());
                dto.setApprovalWorkflow(aw);
            }

            // Map children (PlanDetail → PlanDetailDTO)
            List<PlanDetailListDTO> details = plan.getPlanPlanDetails().stream().map(detail -> {
                        PlanDetailListDTO d = new PlanDetailListDTO();
                        d.setId(detail.getId());
                        d.setSerial(detail.getQrCode());
                        d.setManager(detail.getManager());
                        d.setDetail(detail.getDetail());
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
                    })
                    .sorted(Comparator.comparing(PlanDetailListDTO::getId).reversed())
                    .toList();

            dto.setDetails(details);
            return dto;
        }).toList();
    }

    public void updateStatus(Long id, Integer status) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        plan.setStatus(status);
        planRepository.save(plan);
    }

    @Transactional
    public Plan createPlan(PlanRequest request, String userName) {
        // Kiểm tra mã kế hoạch đã tồn tại hay chưa
        for(DeviceRequest deviceRequest: request.getDevices()){
            deviceRequest.setPlanDetailId(null);
        }
        Plan plan = preparePlanForCreate(request.getPlan());
        plan = planRepository.save(plan);

        List<PlanDetail> details = buildPlanDetails(plan, request, userName);
        List<PlanDetail> savedDetails = addDetailToSampleReport(details);// chuyển detail sang JSON
        planDetailRepository.saveAll(savedDetails);
        autoCreatePlanResult(details);
        if(plan.getPlanType().getCode().equals("DAILYCHECK")){
            createApproveForManager(plan, savedDetails);
        }
        return plan;
    }

    public void createApproveForManager(Plan plan, List<PlanDetail> planDetails) {
        LocalDate fromDate = plan.getFromDate().toLocalDate();
        LocalDate toDate = plan.getToDate().toLocalDate();
        for (PlanDetail planDetail : planDetails) {
            String userManager = null;
            DeviceDTO deviceDTO = deviceService.get(planDetail.getDevice().getId());
            if (deviceDTO.getTeam() == null ) {
                userManager = deviceDTO.getBranch().getManager();
            } else {
                userManager = deviceDTO.getTeam().getManager();
            }
            LocalDate current = fromDate;
            while (!current.isAfter(toDate)) {
                if (current.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    Approval approval = new Approval();
                    // Phân biệt bằng createdAt: gán theo ngày thứ 7
                    approval.setCreatedAt(current.atStartOfDay());
                    approval.setUpdatedAt(LocalDateTime.now());
                    approval.setCreatedBy("admin");
                    approval.setUpdatedBy(null);
                    approval.setStatus(1);
                    approval.setEntityId(planDetail.getId());
                    approval.setEntityType("plan_details");
                    approval.setUsername(userManager);
                    approvalRepository.save(approval);
                }
                current = current.plusDays(1);
            }
        }
    }


    public List<PlanDetail> addDetailToSampleReport(List<PlanDetail> planDetails) {
        ObjectMapper mapper = new ObjectMapper();
        // Đăng ký module hỗ trợ Java 8 Date/Time
        mapper.registerModule(new JavaTimeModule());

// Tùy chọn: disable timestamp để xuất ra ISO-8601 thay vì số mili giây
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        List<PlanDetail> savedDetails = planDetails.stream()
                .filter(detail -> detail.getSampleReport() != null)
                .map(detail -> {// với mỗi detail, ta sẽ chuyển đổi detail.getDetail()
                    try {
                        // convert sampleReport sang JSON string
                        SampleReport sampleReport = sampleReportRepository.findById(detail.getSampleReport().getId()).orElseThrow();// lấy lại đầy đủ sampleReport
                        SampleReportDTO sampleReportDTO = sampleReportService.mapToDTO(sampleReport, new SampleReportDTO());// chuyển sang DTO
                        List<KeyMappingDTO> keyMappingDTOS = keyMappingService.getBySampleReportId(sampleReport.getId());// lấy keyMapping theo sampleReportId
                        sampleReportDTO.setSampleReportKeyMappings(keyMappingDTOS);// gán vào DTO
                        String jsonString = mapper.writeValueAsString(sampleReportDTO);// chuyển DTO sang JSON
                        // gán vào trường detail
                        detail.setDetail(jsonString);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                        // có thể gán giá trị mặc định nếu lỗi
                        detail.setDetail("{}");
                    }
                    return detail; // phải return đối tượng
                })
                .toList();

        return savedDetails;
    }

    @Transactional
    public PlanUpdateResponse updatePlan(Long id, String userName, PlanRequest request) {

        PlanUpdateResponse planUpdateResponse = new PlanUpdateResponse();
        String message = "Cập nhật kế hoạch thành công";
        String deviceNames ="";
        planUpdateResponse.setMessage(message);
        planUpdateResponse.setStatus("SUCCESS");
        for (DeviceRequest deviceRequest : request.getDevices()) {
            if(deviceRequest.getPlanDetailId() != null){
                // 1. Lấy PlanDetail từ DB (hoặc từ Map đã chuẩn bị trước)
                PlanDetail planDetail = planDetailRepository.findById(deviceRequest.getPlanDetailId())
                        .orElseThrow(() -> new RuntimeException("PlanDetail not found"));
                Integer count = planResultDetailRepository.getCountByPlanDetailId(planDetail.getId() );
                if(count > 0){

                    planUpdateResponse.setStatus("FAIL");
                    deviceNames =  planDetail.getDevice().getName() ;
                    continue;
                }else {
                    // Nếu không có dữ liệu kiểm tra nào , ta sẽ tiến hành cập nhật lại PlanDetail
                    for( PLanDetailRequest  pLanDetailRequest :request.getPlanDetails()){
                        if(pLanDetailRequest.getDeviceGroup().getId() == planDetail.getDeviceGroup().getId()){
                            // gán lại deviceGroup và sampleReport nếu có sự thay đổi
                            planDetail.setSampleReport(sampleReportRepository.findById(pLanDetailRequest.getSampleReport().getId()).orElseThrow());
                            List<PlanDetail> planDetailList = new ArrayList<>();
                            planDetailList.add(planDetail);
                            planDetailRepository.saveAll(addDetailToSampleReport(planDetailList));
                            break;
                        }
                    }
                }
                // 2. Chuyển đổi Manager từ Request thành Set (để dùng phương thức contains nhanh hơn)
                String reqManagerStr = deviceRequest.getManager();
                Set<String> managerRequestSet = (reqManagerStr != null && !reqManagerStr.isEmpty())
                        ? new HashSet<>(Arrays.asList(reqManagerStr.split(",")))
                        : new HashSet<>();

                // 3. Chuyển đổi Manager từ DB thành List/Set
                String existManagerStr = planDetail.getManager();
                List<String> managerExistList = (existManagerStr != null && !existManagerStr.isEmpty())
                        ? Arrays.asList(existManagerStr.split(","))
                        : Collections.emptyList();

                // 4. LỌC: Những người có trong Exist nhưng KHÔNG có trong Request (Danh sách bị loại bỏ)
                List<String> removedManagers = managerExistList.stream()
                        .map(String::trim) // Xóa khoảng trắng thừa nếu có
                        .filter(m -> !managerRequestSet.contains(m.trim()))
                        .collect(Collectors.toList());

                // In kết quả hoặc xử lý tiếp
                System.out.println("Managers bị xóa cho thiết bị " + planDetail.getId() + ": " + removedManagers);
                for(String mgr : removedManagers){
                    Integer countResultDetail = planResultDetailRepository.countByPlanDetailId(planDetail.getId(), mgr);
                    if(countResultDetail > 0){
                        planUpdateResponse.setStatus("WARNING");
                        planUpdateResponse.setMessage("Không thể cập nhật kế hoạch do người phụ trách : " + mgr + " đã có dữ liệu kiểm tra.");
                        System.out.println("Không thể cập nhật kế hoạch do người phụ trách " + mgr + " đã có dữ liệu kiểm tra.");
                        return planUpdateResponse;
                    }
                }
            }
        }
        createLog(id,userName);

        Plan exist = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        updatePlanFields(exist, request.getPlan());
        planRepository.save(exist);

        List<PlanDetail> newDetails = buildPlanDetails(exist, request, userName);
        ObjectMapper mapper = new ObjectMapper();
        List<PlanDetail> savedDetails = addDetailToSampleReport(newDetails);// chuyển detail sang JSON
        planDetailRepository.saveAll(savedDetails);
        autoCreatePlanResult(savedDetails);
        if (planUpdateResponse.getStatus().equals("SUCCESS")) {
            planUpdateResponse.setMessage("Cập nhật kế hoạch thành công");
        }else{
            planUpdateResponse.setMessage("Cập nhật kế hoạch thành công nhưng có một số thiết bị không được cập nhật do đã có dữ liệu kiểm tra. Thiết bị: " + deviceNames);
        }
        return planUpdateResponse;
    }
    @Transactional
    public void createLog(Long id, String userName) {
        try {
            // 1. Lấy dữ liệu đã được detach
            PlanSaveDetailLog planRequest = getPlanDetail2(id);

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            String planJson = mapper.writeValueAsString(planRequest);

            // 3. Đếm version và lưu log
            Integer countLog = detailLogRepository.countByEntityTypeAndEntityId("plans", id);

            DetailLogDTO detailLog = new DetailLogDTO();
            detailLog.setEntityType("plans");
            detailLog.setEntityId(id);
            detailLog.setDetail(planJson);
            detailLog.setVersion(String.valueOf(countLog + 1));
            detailLog.setCreatedAt(java.time.LocalDateTime.now());
            detailLog.setLoggedAt(java.time.LocalDateTime.now());
            detailLog.setCreatedBy(userName);
            detailLog.setStatus(1);

            detailLogService.create(detailLog);
        } catch (Exception e) {
            throw new RuntimeException("Error while processing Plan log", e);
        }
    }
    public void autoCreatePlanResult(List<PlanDetail> planDetails) {
        for (PlanDetail planDetail : planDetails) {
            Plan plan = planDetail.getPlan();
            if (plan.getPlanType().getCode().equals("DAILYCHECK")) {
                // tạo plan Result cho tháng hiện tại
                // Lấy tháng hiện tại
                YearMonth currentMonth = YearMonth.now();
                String manager = deviceRepository.findById(planDetail.getDevice().getId()).orElseThrow().getUserManager();
                Duration duration = Duration.between(plan.getFromDate(), plan.getToDate());
                Integer startDay = plan.getFromDate().getDayOfMonth();
                // Duyệt từng ngày trong tháng
                for (int day = 0; day <= duration.toDays(); day++) {
                    LocalDate date = currentMonth.atDay(startDay);
                    // Trả về LocalDateTime lúc 00:00 của ngày đó
                    LocalDateTime dateTime = date.atTime(17, 00, 00);
                    List<PlanResult> planResultCheck = planResultRepository.findByPlanDetailIdAndDateTestLike(planDetail.getId(), "%"+date.toString()+"%");
                    if (planResultCheck.size() > 0) {
                        for (PlanResult pr : planResultCheck) {
                            pr.setUserTest(planDetail.getManager());
                            planResultRepository.save(pr);
                        }
                    }else {
                        PlanResult planResult = new PlanResult();
                        planResult.setPlanDetail(planDetail);
                        planResult.setCreatedAt(java.time.LocalDateTime.now());
                        planResult.setUpdatedAt(java.time.LocalDateTime.now());
                        planResult.setCreatedBy("system");
                        planResult.setUpdatedBy(null);
                        planResult.setStatus(1);
                        planResult.setStatusRepair("1");
                        planResult.setNote("");
                        planResult.setDateTest(dateTime);
                        planResult.setUserTest(planDetail.getManager());
                        planResultService.create(planResultService.mapToDTO(planResult, new PlanResultDTO()));
                    }
                    startDay++;
                }
            }
        }
    }
// ================================================================
// COMMON SUPPORT METHODS
// ================================================================

    private Plan preparePlanForCreate(Plan p) {
        p.setId(null);
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());
        return p;
    }

    private void updatePlanFields(Plan exist, Plan newValue) {
        exist.setName(newValue.getName());
        exist.setCode(newValue.getCode());
        exist.setFrequency(newValue.getFrequency());
        exist.setPlanNumber(newValue.getPlanNumber());
        exist.setFromDate(newValue.getFromDate());
        exist.setToDate(newValue.getToDate());
        exist.setDescription(newValue.getDescription());
        exist.setPlanType(newValue.getPlanType());
        exist.setApprovalWorkflow(newValue.getApprovalWorkflow());
        exist.setBranch(newValue.getBranch());
        exist.setFactory(newValue.getFactory());
        exist.setTeam(newValue.getTeam());
        exist.setUserPerformer(newValue.getUserPerformer());
        exist.setUpdatedAt(LocalDateTime.now());
    }

// ================================================================
// CORE: Build PlanDetails — ĐÃ THỐNG NHẤT HOÀN TOÀN
// ================================================================

    private List<PlanDetail> buildPlanDetails(Plan plan, PlanRequest request, String userName) {
        List<PlanDetail> list = new ArrayList<>();
        Set<Long> processedDeviceIds = new HashSet<>();

        // 1. Tạo bản đồ (Map) để truy xuất nhanh các PlanDetail cũ đang có trong Database (nếu cần)
        // Hoặc đơn giản là xử lý trực tiếp dựa trên PlanDetailId từ Request

        // -----------------------------
        // 1. Trường hợp group + sample
        // -----------------------------
        if (request.getPlanDetails() != null && !request.getPlanDetails().isEmpty() &&
                request.getDevices() != null && !request.getDevices().isEmpty()) {

            for (PLanDetailRequest detailRequest : request.getPlanDetails()) {
                for (DeviceRequest deviceRequest : request.getDevices()) {

                    // KIỂM TRA: Nếu có planDetailId -> Đây là hàng cũ, cần cập nhật Manager
                    if (deviceRequest.getPlanDetailId() != null) {
                        // Logic cập nhật manager cho kế hoạch cũ sẽ nằm ở Trường hợp 2 để tránh lặp logic
                        continue;
                    }

                    Integer isHadDataPlanReport = deviceRequest.getDevice().getIsHadDataPlanReport() == null ? 0 : deviceRequest.getDevice().getIsHadDataPlanReport();

                    if (isHadDataPlanReport == 0) {
                        if (deviceRequest.getDevice().getGroup().getId() == detailRequest.getDeviceGroup().getId()) {
                            PlanDetail d = new PlanDetail();
                            d.setPlan(plan);
                            d.setDeviceGroup(convertDeviceGroup(detailRequest.getDeviceGroup()));
                            d.setSampleReport(convertSampleReport(detailRequest.getSampleReport()));

                            if (deviceRequest.getDevice() != null && deviceRequest.getDevice().getId() != null) {
                                d.setDevice(convertDevice(deviceRequest.getDevice()));
                                processedDeviceIds.add(deviceRequest.getDevice().getId());
                            }

                            // Set common fields
                            setCommonFields(d, deviceRequest, userName, true); // true = tạo mới
                            list.add(d);
                        }
                    }
                }
            }
        }

        // -----------------------------
        // 2. Xử lý cập nhật Manager cho Kế hoạch cũ & Thêm thiết bị lẻ mới
        // -----------------------------
        if (request.getDevices() != null) {
            for (DeviceRequest dr : request.getDevices()) {

                PlanDetail d;
                boolean isNew = false;

                // KIỂM TRA LÀ CŨ HAY MỚI
                if (dr.getPlanDetailId() != null) {
                    // ĐÂY LÀ KẾ HOẠCH CŨ -> Cần cập nhật Manager
                    // Bạn cần lấy PlanDetail cũ từ DB hoặc từ plan.getPlanDetails() hiện có
                    d = plan.getPlanPlanDetails().stream()
                            .filter(pd -> pd.getId().equals(dr.getPlanDetailId()))
                            .findFirst()
                            .orElse(new PlanDetail()); // Nếu không tìm thấy thì coi như mới (phòng lỗi)

                    if (d.getId() == null) isNew = true;
                } else {
                    // ĐÂY LÀ KẾ HOẠCH MỚI
                    Long currentDeviceId = (dr.getDevice() != null) ? dr.getDevice().getId() : null;
                    if (currentDeviceId != null && processedDeviceIds.contains(currentDeviceId)) {
                        continue;
                    }
                    d = new PlanDetail();
                    d.setPlan(plan);
                    isNew = true;
                }

                // CẬP NHẬT THÔNG TIN (Dùng chung cho cả cũ và mới)
                if (dr.getDevice() != null) {
                    d.setDevice(convertDevice(dr.getDevice()));
                    if (dr.getDevice().getGroup() != null) {
                        d.setDeviceGroup(convertDeviceGroup(dr.getDevice().getGroup()));
                    }
                }
                for (PLanDetailRequest detailRequest : request.getPlanDetails()) {
                    if (dr.getDevice() != null && dr.getDevice().getGroup() != null &&
                            dr.getDevice().getGroup().getId().equals(detailRequest.getDeviceGroup().getId())) {
                        d.setDeviceGroup(convertDeviceGroup(detailRequest.getDeviceGroup()));
                        d.setSampleReport(convertSampleReport(detailRequest.getSampleReport()));
                        break;
                    }
                }
                // Cập nhật Manager và các trường thông tin khác từ Request
                setCommonFields(d, dr, userName, isNew);

                list.add(d);
            }
        }

        return list;
    }

    // Hàm bổ trợ để gán dữ liệu, tránh viết lặp code
    private void setCommonFields(PlanDetail d, DeviceRequest dr, String userName, boolean isNew) {
        d.setManager(dr.getManager()); // Cập nhật manager mới từ request
        d.setQrCode(dr.getQrCode());
        d.setEstimatedTime(dr.getEstimatedTime());
        d.setNameDetail(dr.getNameDetail());
        d.setNote(dr.getNote());

        d.setUpdatedAt(LocalDateTime.now());
        d.setUpdatedBy(userName);

        if (isNew) {
            d.setCreatedAt(LocalDateTime.now());
            d.setCreatedBy(userName);
            d.setStatus(1);
        }
    }

// ================================================================
// DTO → ENTITY MAPPING
// ================================================================

    private DeviceGroup convertDeviceGroup(DeviceGroupDTO dto) {
        if (dto == null) return null;
        DeviceGroup g = new DeviceGroup();
        g.setId(dto.getId());
        return g;
    }

    private DeviceGroup convertDeviceGroup(DeviceGroup entity) {
        if (entity == null) return null;
        DeviceGroup g = new DeviceGroup();
        g.setId(entity.getId());
        return g;
    }

    private SampleReport convertSampleReport(SampleReportDTO dto) {
        if (dto == null) return null;
        SampleReport s = new SampleReport();
        s.setId(dto.getId());
        return s;
    }

    private Device convertDevice(DeviceDTO dto) {
        if (dto == null) return null;
        Device d = new Device();
        d.setId(dto.getId());
        return d;
    }

    private PlanDTO mapToDTO(final Plan plan, final PlanDTO dto) {
        dto.setId(plan.getId());
        dto.setCode(plan.getCode());
        dto.setName(plan.getName());
        dto.setFrequency(plan.getFrequency());
        dto.setPlanNumber(plan.getPlanNumber());
        dto.setUserPerformer(plan.getUserPerformer());
        dto.setFromDate(plan.getFromDate());
        dto.setToDate(plan.getToDate());
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

        if (plan.getTeam() != null) {
            Team teamCopy = new Team();
            teamCopy.setId(plan.getTeam().getId());
            teamCopy.setCode(plan.getTeam().getCode());
            teamCopy.setName(plan.getTeam().getName());
            teamCopy.setDescription(plan.getTeam().getDescription());
            teamCopy.setCreatedAt(plan.getTeam().getCreatedAt());
            teamCopy.setUpdatedAt(plan.getTeam().getUpdatedAt());
            teamCopy.setCreatedBy(plan.getTeam().getCreatedBy());
            teamCopy.setUpdatedBy(plan.getTeam().getUpdatedBy());
            teamCopy.setStatus(plan.getTeam().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            teamCopy.setTeamLines(null);
            teamCopy.setTeamDevices(null);

            dto.setTeam(teamCopy);
        } else {
            dto.setTeam(null);
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
        if (plan.getPlanPlanDetails() != null) {
            List<PlanDetailDTO> planDetailDTOS = plan.getPlanPlanDetails().stream()
                    .map(planDetail -> planDetailService.mapToDTO(planDetail, new PlanDetailDTO()))
                    .toList();
            // xoa quan he de tranh vong lap
            planDetailDTOS.forEach(detailDTO -> {
                detailDTO.setPlan(null);
            });
            dto.setPlanDetails(planDetailDTOS);
        }
        return dto;
    }


    private Plan mapToEntity(final PlanDTO planDTO, final Plan plan) {
        plan.setCode(planDTO.getCode());
        plan.setName(planDTO.getName());
        plan.setFrequency(planDTO.getFrequency());
        plan.setPlanNumber(planDTO.getPlanNumber());
        plan.setUserPerformer(planDTO.getUserPerformer());
        plan.setFromDate(planDTO.getFromDate());
        plan.setToDate(planDTO.getToDate());
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

        final Team team = planDTO.getTeam() == null ? null : teamRepository.findById(planDTO.getTeam().getId())
                .orElseThrow(() -> new NotFoundException("team not found"));
        plan.setTeam(team);

        final ApprovalWorkflow approvalWorkflow = planDTO.getApprovalWorkflow() == null ? null :
                approvalWorkflowRepository.findById(planDTO.getApprovalWorkflow().getId())
                        .orElseThrow(() -> new NotFoundException("approvalWorkflow not found"));
        plan.setApprovalWorkflow(approvalWorkflow);

        return plan;
    }

    public void inactiveatePlan(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plan not found"));
        plan.setStatus(0); // Giả sử 0 là trạng thái không hoạt động
        planRepository.save(plan);
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

    public PlanDTO getById(final Long id) {
        return planRepository.findById(id)
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .orElseThrow(NotFoundException::new);
    }
    public PlanDTO getMaintainById(final Long id) {
        return planRepository.findById(id)
                .map(plan -> {
                    PlanDTO dto = mapToDTO(plan, new PlanDTO());

                    if (dto.getPlanDetails() != null) {
                        // Tạo một ArrayList mới từ danh sách cũ để có thể sửa đổi (Sort)
                        List<PlanDetailDTO> mutableDetails = new ArrayList<>(dto.getPlanDetails());

                        mutableDetails.forEach(detail -> {
                            if (detail.getPlanResults() != null) {
                                List<PlanResultDTO> filteredResults = detail.getPlanResults().stream()
                                        .filter(result ->
                                                result.getPlanDetail() != null &&
                                                        result.getPlanDetail().getPlan() != null &&
                                                        result.getPlanDetail().getPlan().getPlanType() != null &&
                                                        "MAINTENANCE".equals(result.getPlanDetail().getPlan().getPlanType().getCode())
                                        )
                                        .collect(Collectors.toList());

                                detail.setPlanResults(filteredResults);
                            }
                        });

                        // Bây giờ bạn có thể sắp xếp trên danh sách mutableDetails này
                        mutableDetails.sort(Comparator.comparing(detail -> {
                            if (detail.getDevice() != null && detail.getDevice().getLine() != null) {
                                return detail.getDevice().getLine().getId();
                            }
                            return Long.MAX_VALUE;
                        }, Comparator.nullsLast(Comparator.naturalOrder())));

                        // Gán lại danh sách đã sắp xếp vào DTO
                        dto.setPlanDetails(mutableDetails);
                    }

                    return dto;
                })
                .orElseThrow(NotFoundException::new);
    }
    public PlanDTO getDailyById(final Long id) {
        return planRepository.findById(id)
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public ApplicationEventPublisher getPublisher() {
        return publisher;
    }
}
