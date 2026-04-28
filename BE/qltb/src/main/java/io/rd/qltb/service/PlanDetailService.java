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
import java.time.LocalDate;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

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

    @PersistenceContext
    private final EntityManager entityManager;

    public PlanDetailService(final PlanDetailRepository planDetailRepository,
                             final PlanRepository planRepository, final DeviceRepository deviceRepository,
                             final SampleReportRepository sampleReportRepository,
                             final DeviceGroupRepository deviceGroupRepository, ApprovalRepository approvalRepository, ApprovalService approvalService, PlanResultDetailRepository planResultDetailRepository, PlanResultService planResultService, PlanResultDetailService planResultDetailService, PlanResultRepository planResultRepository, ApprovalWorkflowRepository approvalWorkflowRepository, ErrorReportRepository errorReportRepository, ErrorReportService errorReportService, EntityManager entityManager) {
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
        this.entityManager = entityManager;
    }


    public List<PlanDetailDTO> getByPlanId(final Long planId) {
        final List<PlanDetail> planDetails = planDetailRepository.findAllByPlanId(planId);

        return planDetails.stream()
                .map(planDetail -> {
                    PlanDetailDTO dto = mapToDTO(planDetail, new PlanDetailDTO());

                    // Khởi tạo mặc định các trường là 0 nếu chúng đang null sau khi mapToDTO
                    dto.setTotalDayCreated(Objects.requireNonNullElse(dto.getTotalDayCreated(), 0));
                    dto.setTotalDayWorking(Objects.requireNonNullElse(dto.getTotalDayWorking(), 0));
                    dto.setTotalDayComplete(Objects.requireNonNullElse(dto.getTotalDayComplete(), 0));
                    dto.setTotalDayOff(Objects.requireNonNullElse(dto.getTotalDayOff(), 0));

                    if (planDetail.getPlanResults() != null) {
                        for (PlanResult planResult : planDetail.getPlanResults()) {
                            switch (planResult.getStatus()) {
                                case 1 -> dto.setTotalDayCreated(dto.getTotalDayCreated() + 1);
                                case 4 -> dto.setTotalDayWorking(dto.getTotalDayWorking() + 1);
                                case 5 -> dto.setTotalDayComplete(dto.getTotalDayComplete() + 1);
                                case 15 -> dto.setTotalDayOff(dto.getTotalDayOff() + 1);
                            }
                        }
                    }

                    // In kết quả kiểm tra
                    System.out.println("--- Kết quả Mapping ---");
                    System.out.println("ID: " + planId);
                    System.out.println("Created: " + dto.getTotalDayCreated());
                    System.out.println("Working: " + dto.getTotalDayWorking());
                    System.out.println("Complete: " + dto.getTotalDayComplete());
                    System.out.println("Off: " + dto.getTotalDayOff());

                    return dto;
                })
                .toList();
    }

    /**
     * Hàm bổ trợ để xử lý null khi cộng dồn (null-safe)
     */
    private Integer coalesce(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * Lấy danh sách thiết bị tham gia kế hoạch kiểm tra hàng ngày (DAILYCHECK).
     * Group theo deviceId, lấy planDetail mới nhất cho mỗi thiết bị.
     * Hỗ trợ filter theo tên ngành (branch), tổ (team), dây chuyền (line).
     */
    /**
     * Lấy danh sách thiết bị tham gia kế hoạch kiểm tra hàng ngày (DAILYCHECK) có phân trang.
     */
    @Transactional
    public Page<PlanDetailDTO> findDailyCheckDevicesPaged(Map<String, Object> filters, int page, int size) {
        int pageSize = size > 0 ? size : 10;

        DailyCheckSearchContext context = buildDailyCheckSearchContext(filters);

        var cb = entityManager.getCriteriaBuilder();

        // 1. Khởi tạo Query lấy dữ liệu
        var cq = cb.createQuery(PlanDetail.class);
        var root = cq.from(PlanDetail.class);

        // 2. Khởi tạo Query đếm tổng
        var countQuery = cb.createQuery(Long.class);
        var countRoot = countQuery.from(PlanDetail.class);

        // 3. Xây dựng Predicates đồng nhất (có lọc theo khoảng thời gian)
        Predicate[] dataPredicates = buildDailyCheckPredicates(filters, cb, root, context.startDate, context.endDate);
        Predicate[] countPredicates = buildDailyCheckPredicates(filters, cb, countRoot, context.startDate, context.endDate);

        // 4. Thực thi truy vấn lấy dữ liệu trang
        cq.where(dataPredicates);
        cq.orderBy(cb.desc(root.get("id")));

        var query = entityManager.createQuery(cq);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        List<PlanDetailDTO> dtos = query.getResultList().stream()
                .map(pd -> {
                    PlanDetailDTO dto = mapToDTO(pd, new PlanDetailDTO());
                    enrichDeviceInfo(dto, pd);
                    return dto;
                })
                .toList();

        // 5. Bổ sung thống kê tổng hợp (OK, Bất thường, Lỗi...) cho từng thiết bị TRONG KHOẢNG THỜI GIAN
        enrichDailyCheckSummaryStatsRange(dtos, context.startDate, context.endDate);

        // 6. Thực thi truy vấn đếm tổng số bản ghi
        countQuery.select(cb.count(countRoot)).where(countPredicates);
        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(dtos, PageRequest.of(page, pageSize), totalRecords);
    }

    /**
     * Lấy toàn bộ danh sách thiết bị kiểm tra hàng ngày không phân trang (dùng cho export).
     */
    @Transactional
    public List<PlanDetailDTO> findDailyCheckDevicesAll(Map<String, Object> filters) {
        DailyCheckSearchContext context = buildDailyCheckSearchContext(filters);

        var cb = entityManager.getCriteriaBuilder();
        var cq = cb.createQuery(PlanDetail.class);
        var root = cq.from(PlanDetail.class);

        Predicate[] predicates = buildDailyCheckPredicates(filters, cb, root, context.startDate, context.endDate);

        cq.where(predicates);
        cq.orderBy(cb.desc(root.get("id")));

        List<PlanDetailDTO> dtos = entityManager.createQuery(cq).getResultList().stream()
                .map(pd -> {
                    PlanDetailDTO dto = mapToDTO(pd, new PlanDetailDTO());
                    enrichDeviceInfo(dto, pd);
                    return dto;
                })
                .toList();

        enrichDailyCheckSummaryStatsRange(dtos, context.startDate, context.endDate);

        return dtos;
    }

    private DailyCheckSearchContext buildDailyCheckSearchContext(Map<String, Object> filters) {
        Integer fromMonth = filters.get("fromMonth") != null ? Integer.parseInt(filters.get("fromMonth").toString()) : LocalDate.now().getMonthValue();
        Integer fromYear = filters.get("fromYear") != null ? Integer.parseInt(filters.get("fromYear").toString()) : LocalDate.now().getYear();
        Integer toMonth = filters.get("toMonth") != null ? Integer.parseInt(filters.get("toMonth").toString()) : fromMonth;
        Integer toYear = filters.get("toYear") != null ? Integer.parseInt(filters.get("toYear").toString()) : fromYear;

        java.time.LocalDateTime startDate = java.time.LocalDateTime.of(fromYear, fromMonth, 1, 0, 0, 0);
        java.time.YearMonth toYearMonth = java.time.YearMonth.of(toYear, toMonth);
        java.time.LocalDateTime endDate = toYearMonth.atEndOfMonth().atTime(23, 59, 59);

        // Xóa các tham số đặc biệt
        filters.remove("fromMonth");
        filters.remove("fromYear");
        filters.remove("toMonth");
        filters.remove("toYear");
        filters.remove("month");
        filters.remove("year");

        return new DailyCheckSearchContext(startDate, endDate);
    }

    private static class DailyCheckSearchContext {
        final java.time.LocalDateTime startDate;
        final java.time.LocalDateTime endDate;

        DailyCheckSearchContext(java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    /**
     * Tính toán tổng hợp các chỉ số (OK, Bất thường, Đã điều chỉnh, Lỗi) cho danh sách thiết bị trong khoảng thời gian.
     */
    private void enrichDailyCheckSummaryStatsRange(List<PlanDetailDTO> dtos, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        if (dtos == null || dtos.isEmpty()) return;

        List<Long> deviceIds = dtos.stream()
                .filter(dto -> dto.getDevice() != null)
                .map(dto -> dto.getDevice().getId())
                .distinct()
                .toList();

        if (deviceIds.isEmpty()) return;

        // 1. Thống kê kết quả từ PlanResultDetail trong dải ngày
        String prdQuery = "SELECT pr.planDetail.device.id, prd.result, COUNT(prd.id) " +
                "FROM PlanResultDetail prd " +
                "JOIN prd.planResult pr " +
                "WHERE pr.planDetail.device.id IN :ids " +
                "AND pr.dateTest >= :startDate AND pr.dateTest <= :endDate " +
                "AND (prd.result = 'OK' OR prd.result = 'Đã điều chỉnh' OR prd.result = 'Có bất thường') " +
                "GROUP BY pr.planDetail.device.id, prd.result";

        List<Object[]> prdResults = entityManager.createQuery(prdQuery, Object[].class)
                .setParameter("ids", deviceIds)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        // 2. Thống kê số lượng ErrorReport (Lỗi) trong dải ngày
        String erQuery = "SELECT pr.planDetail.device.id, er.isRepaired, COUNT(er.id) " +
                "FROM ErrorReport er " +
                "JOIN er.planResult pr " +
                "WHERE pr.planDetail.device.id IN :ids " +
                "AND er.timeReported >= :startDate AND er.timeReported <= :endDate " +
                "AND (er.status IS NULL OR er.status != 10) " +
                "GROUP BY pr.planDetail.device.id, er.isRepaired";

        List<Object[]> erResults = entityManager.createQuery(erQuery, Object[].class)
                .setParameter("ids", deviceIds)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        // 3. Mapping kết quả vào DTO
        for (PlanDetailDTO dto : dtos) {
            dto.setCountOk(0L);
            dto.setCountAbnormal(0L);
            dto.setCountAdjusted(0L);
            dto.setTotalErrors(0L);
            dto.setFixedErrors(0L);

            if (dto.getDevice() == null) continue;
            Long deviceId = dto.getDevice().getId();

            for (Object[] res : prdResults) {
                if (res[0].equals(deviceId)) {
                    String result = (String) res[1];
                    Long count = (Long) res[2];
                    if ("OK".equals(result)) dto.setCountOk(count);
                    else if ("Có bất thường".equals(result)) dto.setCountAbnormal(count);
                    else if ("Đã điều chỉnh".equals(result)) dto.setCountAdjusted(count);
                }
            }

            for (Object[] res : erResults) {
                if (res[0].equals(deviceId)) {
                    Boolean isRepaired = (Boolean) res[1];
                    Long count = (Long) res[2];
                    dto.setTotalErrors(dto.getTotalErrors() + count);
                    if (Boolean.TRUE.equals(isRepaired)) {
                        dto.setFixedErrors(dto.getFixedErrors() + count);
                    }
                }
            }
        }
    }

    private Predicate[] buildDailyCheckPredicates(Map<String, Object> filters, CriteriaBuilder cb, Root<PlanDetail> root, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        List<Predicate> predicates = new ArrayList<>();

        // Join các quan hệ cần thiết
        Join<PlanDetail, Plan> planJoin = root.join("plan", JoinType.INNER);
        Join<Plan, PlanType> planTypeJoin = planJoin.join("planType", JoinType.INNER);
        Join<PlanDetail, Device> deviceJoin = root.join("device", JoinType.LEFT);

        // Filter mặc định: DAILYCHECK và không bị xóa
        predicates.add(cb.equal(planTypeJoin.get("code"), "DAILYCHECK"));
        predicates.add(cb.notEqual(planJoin.get("status"), 10));
        predicates.add(cb.notEqual(root.get("status"), 10));

        // Lọc theo khoảng thời gian: Kế hoạch phải gối (overlap) với khoảng thời gian chọn
        // (plan.fromDate <= :endDate) AND (plan.toDate >= :startDate)
        if (startDate != null && endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(planJoin.get("fromDate"), endDate));
            predicates.add(cb.greaterThanOrEqualTo(planJoin.get("toDate"), startDate));
        }

        // Filter người dùng
        filters.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                if (key.equals("device.branch.name")) {
                    Join<Device, Branch> branchJoin = deviceJoin.join("branch", JoinType.LEFT);
                    predicates.add(cb.like(cb.lower(branchJoin.get("name")), "%" + value.toString().toLowerCase() + "%"));
                } else if (key.equals("device.team.name")) {
                    Join<Device, Team> teamJoin = deviceJoin.join("team", JoinType.LEFT);
                    predicates.add(cb.like(cb.lower(teamJoin.get("name")), "%" + value.toString().toLowerCase() + "%"));
                } else if (key.equals("device.line.name")) {
                    Join<Device, Line> lineJoin = deviceJoin.join("line", JoinType.LEFT);
                    predicates.add(cb.like(cb.lower(lineJoin.get("name")), "%" + value.toString().toLowerCase() + "%"));
                } else if (key.startsWith("device.")) {
                    String field = key.replace("device.", "");
                    predicates.add(cb.like(cb.lower(deviceJoin.get(field)), "%" + value.toString().toLowerCase() + "%"));
                } else if (key.startsWith("plan.")) {
                    String field = key.replace("plan.", "");
                    predicates.add(cb.like(cb.lower(planJoin.get(field)), "%" + value.toString().toLowerCase() + "%"));
                } else if (key.equals("id")) {
                    predicates.add(cb.equal(root.get("id"), value));
                } else {
                    predicates.add(cb.like(cb.lower(root.get(key).as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                }
            }
        });

        return predicates.toArray(new Predicate[0]);
    }

    public List<PlanDetailDTO> getDailyCheckDevices(String branch, String team, String line) {
        List<PlanDetail> allDetails = planDetailRepository.findAllDailyCheckPlanDetails();

        // Trả về toàn bộ danh sách, lọc theo NAME (LIKE, case-insensitive)
        return allDetails.stream()
                .filter(pd -> pd.getDevice() != null)
                .filter(pd -> branch == null || branch.isEmpty() ||
                        (pd.getDevice().getBranch() != null && pd.getDevice().getBranch().getName() != null &&
                                pd.getDevice().getBranch().getName().toLowerCase().contains(branch.toLowerCase())))
                .filter(pd -> team == null || team.isEmpty() ||
                        (pd.getDevice().getTeam() != null && pd.getDevice().getTeam().getName() != null &&
                                pd.getDevice().getTeam().getName().toLowerCase().contains(team.toLowerCase())))
                .filter(pd -> line == null || line.isEmpty() ||
                        (pd.getDevice().getLine() != null && pd.getDevice().getLine().getName() != null &&
                                pd.getDevice().getLine().getName().toLowerCase().contains(line.toLowerCase())))
                .map(pd -> {
                    PlanDetailDTO dto = mapToDTO(pd, new PlanDetailDTO());
                    // Bổ sung thông tin branch, team, line vào device
                    enrichDeviceInfo(dto, pd);
                    return dto;
                })
                .sorted(Comparator.comparing(PlanDetailDTO::getId).reversed())
                .toList();
    }

    /**
     * Bổ sung thông tin branch, team, line vào device trong PlanDetailDTO
     */
    private void enrichDeviceInfo(PlanDetailDTO dto, PlanDetail pd) {
        if (dto.getDevice() == null) return;

        if (pd.getDevice().getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(pd.getDevice().getBranch().getId());
            branchCopy.setCode(pd.getDevice().getBranch().getCode());
            branchCopy.setName(pd.getDevice().getBranch().getName());
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            branchCopy.setFactory(null);
            branchCopy.setSampleReports(null);
            dto.getDevice().setBranch(branchCopy);
        }
        if (pd.getDevice().getTeam() != null) {
            Team teamCopy = new Team();
            teamCopy.setId(pd.getDevice().getTeam().getId());
            teamCopy.setCode(pd.getDevice().getTeam().getCode());
            teamCopy.setName(pd.getDevice().getTeam().getName());
            teamCopy.setBranch(null);
            teamCopy.setTeamDevices(null);
            teamCopy.setTeamLines(null);
            dto.getDevice().setTeam(teamCopy);
        }
        if (pd.getDevice().getLine() != null) {
            Line lineCopy = new Line();
            lineCopy.setId(pd.getDevice().getLine().getId());
            lineCopy.setCode(pd.getDevice().getLine().getCode());
            lineCopy.setName(pd.getDevice().getLine().getName());
            lineCopy.setTeam(null);
            lineCopy.setLineDevices(null);
            dto.getDevice().setLine(lineCopy);
        }
    }

    /**
     * Lấy PlanCheckDTO cho 1 planDetail, lọc planResultDetail theo tháng/năm.
     * Trả dữ liệu giống getPlanCheckDetail nhưng filter theo month/year.
     * Set planDetail.createdAt = ngày 1 tháng đó để ViewEvaluatePage hiển thị đúng.
     */
    public PlanCheckDTO getPlanCheckDetailByMonth(Long id, String entityType, int month, int year) {
        // Lấy thông tin PlanDetail (giống getPlanCheckDetail)
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        PlanDetailDTO planDetailDTO = mapToDTO(planDetail, new PlanDetailDTO());

        // Set thêm thông tin liên quan
        if (planDetail.getDeviceGroup() != null) {
            planDetailDTO.setSampleReport(planDetail.getSampleReport());
            planDetailDTO.getSampleReport().setDeviceGroups(null);
            planDetailDTO.getSampleReport().setBranch(null);
            planDetailDTO.getSampleReport().setApprovalWorkflow(null);
            planDetailDTO.getSampleReport().setSampleReportKeyMappingDeviceSampleReports(null);
            planDetailDTO.getSampleReport().setSampleReportKeyMappings(null);
        }
        if (planDetail.getDevice().getBranch() != null) {
            planDetailDTO.getDevice().setBranch(planDetail.getDevice().getBranch());
            planDetailDTO.getDevice().getBranch().getFactory().setFactoryBranches(null);
            planDetailDTO.getDevice().getBranch().setBranchTeams(null);
            planDetailDTO.getDevice().getBranch().setBranchDevices(null);
            planDetailDTO.getDevice().getBranch().setSampleReports(null);
        }
        if (planDetail.getDevice().getLine() != null) {
            planDetailDTO.getDevice().setLine(planDetail.getDevice().getLine());
            planDetailDTO.getDevice().getLine().setTeam(null);
            planDetailDTO.getDevice().getLine().setLineDevices(null);
        }
        if (planDetail.getDevice().getTeam() != null) {
            planDetailDTO.getDevice().setTeam(planDetail.getDevice().getTeam());
            planDetailDTO.getDevice().getTeam().setBranch(null);
            planDetailDTO.getDevice().getTeam().setTeamDevices(null);
            planDetailDTO.getDevice().getTeam().setTeamLines(null);
        }

        // Override createdAt để ViewEvaluatePage hiển thị đúng tháng được chọn
        java.time.LocalDateTime monthDate = java.time.LocalDateTime.of(year, month, 1, 0, 0, 0);
        planDetailDTO.setCreatedAt(monthDate);

        PlanCheckDTO planCheckDTO = new PlanCheckDTO();
        planCheckDTO.setPlanDetail(planDetailDTO);

        // Lấy Approval
        final List<Approval> approvals = approvalRepository.findByEntityTypeAndEntityId(entityType, id);
        List<ApprovalDTO> approvalDTOS = approvals.stream()
                .map(approval -> approvalService.mapToDTO(approval, new ApprovalDTO()))
                .toList();
        planCheckDTO.setApprovals(approvalDTOS);

        // Lấy PlanResultDetail LỌC THEO DEVICE ID VÀ THÁNG/NĂM (để xem báo cáo chéo giữa các kế hoạch)
        List<PlanResultDetail> planResultDetails = planResultDetailRepository.getByDeviceIdAndMonth(planDetail.getDevice().getId(), month, year);
        List<PlanResultDetailDTO> planResultDetailDTOS = planResultDetails.stream()
                .map(planResultDetail -> planResultDetailService.mapToDTO(planResultDetail, new PlanResultDetailDTO()))
                .toList();
        planCheckDTO.setPlanResultDetail(planResultDetailDTOS);

        // Lấy ErrorReport
        List<ErrorReportDTO> errorReportDTOS = errorReportRepository.findByPlanDetailId(id).stream()
                .map(item -> errorReportService.mapToDTO(item, new ErrorReportDTO())).toList();
        planCheckDTO.setErrorReport(errorReportDTOS);

        return planCheckDTO;
    }
    public PlanCheckDTO getPlanCheckDetail(final Long id, String entityType) {
        // Lấy thông tin PlanDetail
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        PlanDetailDTO planDetailDTO = mapToDTO(planDetail, new PlanDetailDTO());
        // Set thêm thông tin liên quan
        if (planDetail.getDeviceGroup() != null) {
        planDetailDTO.setSampleReport(planDetail.getSampleReport());
        planDetailDTO.getSampleReport().setDeviceGroups(null);
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
            sampleReportCopy.setDeviceGroups(null);
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
