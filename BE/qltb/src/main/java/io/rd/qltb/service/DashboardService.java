package io.rd.qltb.service;

import static io.rd.qltb.config.ConstantStatusGlobal.DELETED;

import io.rd.qltb.domain.Acceptance;
import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.PlanDetail;
import io.rd.qltb.domain.ReportDeviceIncident;
import io.rd.qltb.model.AcceptanceDTO;
import io.rd.qltb.model.DashboardDTO;
import io.rd.qltb.model.DashboardFilter;
import io.rd.qltb.model.DeviceDTO;
import io.rd.qltb.model.ReportDeviceIncidentDTO;
import io.rd.qltb.model.response.DashboardMaintenanceExecutionDTO;
import io.rd.qltb.model.response.DashboardPlanDetailRowDTO;
import io.rd.qltb.model.response.DeviceMaintenanceDTO;
import io.rd.qltb.repos.BranchRepository;
import io.rd.qltb.repos.DashboardRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.LongSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Tổng hợp KPI dashboard QLTB và API chi tiết (phân trang + filter).
 * <p>
 * Filter chung: {@link DashboardFilter} — branchId, teamId, groupId, year, month, deviceName,
 * maintenanceDueType (OVERDUE | UPCOMING | ALL).
 */
@Service
public class DashboardService {

    private static final Logger LOG = LoggerFactory.getLogger(DashboardService.class);

    private final DashboardRepository dashboardRepository;
    private final DeviceService deviceService;
    private final BranchRepository branchRepository;
    private final ReportDeviceIncidentService reportDeviceIncidentService;

    public DashboardService(
            final DashboardRepository dashboardRepository,
            final DeviceService deviceService,
            final BranchRepository branchRepository,
            final ReportDeviceIncidentService reportDeviceIncidentService) {
        this.dashboardRepository = dashboardRepository;
        this.deviceService = deviceService;
        this.branchRepository = branchRepository;
        this.reportDeviceIncidentService = reportDeviceIncidentService;
    }

    /**
     * API tổng hợp: chỉ trả số lượng, không trả danh sách dài (danh sách gọi API /details tương ứng).
     */
    public DashboardDTO getDashboardSummary(final DashboardFilter filter) {
        final ResolvedFilter f = resolveFilter(filter);
        final boolean timing = filter != null && Boolean.TRUE.equals(filter.getIncludeTiming());
        final long summaryStart = timing ? System.nanoTime() : 0L;
        final DashboardDTO dto = new DashboardDTO();
        dto.setYear(f.year());
        dto.setMonth(f.month());
        if (f.branchIds() != null) {
            dto.setAppliedBranchIds(new ArrayList<>(f.branchIds()));
        }

        // Mục 1 — thiết bị đang hoạt động: state = 1 (tình trạng "Đang sử dụng" trên FE)
        final long working = timed(timing, dto, "countWorkingDevices",
                () -> dashboardRepository.countWorkingDevices(DELETED, f.branchIds(), f.teamId(), f.groupId()));
        dto.setTotalWorkingDevices(Math.toIntExact(working));

        // Tổng thiết bị (không đếm bản ghi đã xóa mềm)
        final long total = timed(timing, dto, "countAllDevices",
                () -> dashboardRepository.countAllDevices(DELETED, f.branchIds(), f.teamId(), f.groupId()));
        dto.setTotalDevices(Math.toIntExact(total));

        // Mục 2 — thiết bị trong KH bảo trì tháng hiện tại (MAINTENANCE, plan active, estimated_time trong tháng)
        final long monthlyPlan = timed(timing, dto, "countMonthlyMaintenancePlanDevices",
                () -> dashboardRepository.countMonthlyMaintenancePlanDevices(
                        f.branchIds(), f.teamId(), f.groupId(), f.year(), f.month()));
        dto.setTotalDevicesInMaintenance(Math.toIntExact(monthlyPlan));

        // Thiết bị thuộc KH năm (MAINTENANCE, plan active, lọc theo năm)
        final long yearlyPlan = timed(timing, dto, "countYearlyMaintenancePlanDevices",
                () -> dashboardRepository.countYearlyMaintenancePlanDevices(
                        f.branchIds(), f.teamId(), f.groupId(), f.year()));
        dto.setTotalDevicesMaintained(Math.toIntExact(yearlyPlan));

        // Số thiết bị đã hoàn thành bảo dưỡng (plan_detail status = COMPLETED)
        final long maintained = timed(timing, dto, "countMaintainedDevices",
                () -> dashboardRepository.countMaintainedDevices(f.branchIds(), f.teamId(), f.groupId()));
        dto.setTotalMaintained(Math.toIntExact(maintained));

        // Mục 3 — tổng lượt thực hiện bảo trì (bảng plan_results, loại MAINTENANCE)
        final long executions = timed(timing, dto, "countMaintenanceExecutions",
                () -> dashboardRepository.countMaintenanceExecutions(
                        f.branchIds(), f.teamId(), f.groupId(), f.year()));
        dto.setMaintenanceExecutionCount(Math.toIntExact(executions));

        // Mục 5 — nghiệm thu SC/BĐ định kỳ: acceptance type=2, status=3 (đã duyệt)
        final long accepted = timed(timing, dto, "countAcceptedMaintenanceDevices",
                () -> dashboardRepository.countAcceptedMaintenanceDevices(f.branchIds(), f.teamId(), f.groupId()));
        dto.setAcceptedPeriodicMaintenanceDeviceCount(Math.toIntExact(accepted));

        // Mục 5 — đến hạn / quá hạn: dựa chu kỳ maintenance_time + lần BT gần nhất (plan_type MAINTENANCE)
        final String dueType = normalizeDueType(filter != null ? filter.getMaintenanceDueType() : null);
        final long due = timed(timing, dto, "countDueMaintenanceDevices",
                () -> dashboardRepository.countDueMaintenanceDevices(
                        f.branchIds(), f.teamId(), f.groupId(), dueType));
        dto.setDueMaintenanceDeviceCount(Math.toIntExact(due));

        // Mục 6 — theo yêu cầu: devices.status = 3 (ConstantStatusGlobal.APPROVED)
        final long faulty = timed(timing, dto, "countFaultyDevicesByStatus",
                () -> dashboardRepository.countFaultyDevicesByStatus(f.branchIds(), f.teamId(), f.groupId()));
        dto.setFaultyDeviceCount(Math.toIntExact(faulty));

        // Mục 7 — cách 1: đếm biên bản bảng report_device_incidents (không lấy error_reports.severity=0)
        final long incidents = timed(timing, dto, "countCriticalIncidentReports",
                () -> dashboardRepository.countCriticalIncidentReports(f.branchIds(), f.teamId(), f.groupId()));
        dto.setCriticalIncidentReportCount(Math.toIntExact(incidents));

        if (timing) {
            dto.setTotalQueryTimeMs((System.nanoTime() - summaryStart) / 1_000_000L);
            LOG.info("Dashboard summary query timings (ms): {} — total {}",
                    dto.getQueryTimingsMs(), dto.getTotalQueryTimeMs());
        }

        return dto;
    }

    /**
     * Do thoi gian mot lenh COUNT/query (chi phan DB, khong gom mapping DTO).
     */
    private long timed(
            final boolean enabled,
            final DashboardDTO dto,
            final String queryName,
            final LongSupplier query) {
        final long start = System.nanoTime();
        final long result = query.getAsLong();
        if (enabled) {
            dto.putTiming(queryName, (System.nanoTime() - start) / 1_000_000L);
        }
        return result;
    }

    /** Mục 1 — danh sách thiết bị đang hoạt động. */
    public Page<DeviceDTO> getWorkingDevices(final DashboardFilter filter, final Pageable pageable) {
        final ResolvedFilter f = resolveFilter(filter);
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        return dashboardRepository
                .findWorkingDevices(f.branchIds(), f.teamId(), f.groupId(), deviceName, pageable)
                .map(d -> deviceService.mapToDTO(d, new DeviceDTO()));
    }

    /** Mục 2 — chi tiết thiết bị trong KH bảo trì tháng. */
    public Page<DashboardPlanDetailRowDTO> getMonthlyMaintenancePlanDetails(
            final DashboardFilter filter, final Pageable pageable) {
        final ResolvedFilter f = resolveFilter(filter);
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        return dashboardRepository
                .findMonthlyMaintenancePlanDetails(
                        f.branchIds(), f.teamId(), f.groupId(), f.year(), f.month(), deviceName, pageable)
                .map(this::mapPlanDetailRow);
    }

    /** KH bảo trì theo năm (bổ sung cho totalDevicesMaintained). */
    public Page<DashboardPlanDetailRowDTO> getYearlyMaintenancePlanDetails(
            final DashboardFilter filter, final Pageable pageable) {
        final ResolvedFilter f = resolveFilter(filter);
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        return dashboardRepository
                .findYearlyMaintenancePlanDetails(
                        f.branchIds(), f.teamId(), f.groupId(), f.year(), deviceName, pageable)
                .map(this::mapPlanDetailRow);
    }

    /** Thiết bị đã hoàn thành bảo dưỡng (plan_detail status = 5). */
    public Page<DashboardPlanDetailRowDTO> getMaintainedPlanDetails(
            final DashboardFilter filter, final Pageable pageable) {
        final ResolvedFilter f = resolveFilter(filter);
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        return dashboardRepository
                .findMaintainedPlanDetails(f.branchIds(), f.teamId(), f.groupId(), deviceName, pageable)
                .map(this::mapPlanDetailRow);
    }

    /** Mục 3 — từng lượt thực hiện bảo trì (plan_results). */
    public Page<DashboardMaintenanceExecutionDTO> getMaintenanceExecutions(
            final DashboardFilter filter, final Pageable pageable) {
        final ResolvedFilter f = resolveFilter(filter);
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        return dashboardRepository
                .findMaintenanceExecutions(f.branchIds(), f.teamId(), f.groupId(), f.year(), deviceName, pageable)
                .map(this::mapMaintenanceExecution);
    }

    /** Mục 5 — thiết bị đã nghiệm thu (acceptances). */
    public Page<AcceptanceDTO> getAcceptedMaintenanceDetails(
            final DashboardFilter filter, final Pageable pageable) {
        final ResolvedFilter f = resolveFilter(filter);
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        return dashboardRepository
                .findAcceptedMaintenance(f.branchIds(), f.teamId(), f.groupId(), deviceName, pageable)
                .map(this::mapAcceptanceBrief);
    }

    /**
     * Mục 5 — đến hạn bảo dưỡng.
     * Tái sử dụng query {@link io.rd.qltb.repos.DeviceRepository#findMaintenanceData} qua DeviceService
     * (chu kỳ tháng + ngày test gần nhất).
     */
    public Page<DeviceMaintenanceDTO> getDueMaintenanceDetails(
            final DashboardFilter filter, final Pageable pageable) {
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        final ResolvedFilter f = resolveFilter(filter);
        final String dueType = normalizeDueType(filter != null ? filter.getMaintenanceDueType() : null);
        return deviceService.getMaintenanceReport(
                deviceName, f.branchIds(), null, null, null, null, dueType, pageable);
    }

    /** Mục 6 — thiết bị devices.status = 3. */
    public Page<DeviceDTO> getFaultyDevicesByStatus(final DashboardFilter filter, final Pageable pageable) {
        final ResolvedFilter f = resolveFilter(filter);
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        return dashboardRepository
                .findFaultyDevicesByStatus(f.branchIds(), f.teamId(), f.groupId(), deviceName, pageable)
                .map(d -> deviceService.mapToDTO(d, new DeviceDTO()));
    }

    /** Mục 7 — biên bản sự cố nghiêm trọng (report_device_incidents). */
    public Page<ReportDeviceIncidentDTO> getCriticalIncidentDetails(
            final DashboardFilter filter, final Pageable pageable) {
        final ResolvedFilter f = resolveFilter(filter);
        final String deviceName = clean(filter != null ? filter.getDeviceName() : null);
        return dashboardRepository
                .findCriticalIncidentReports(f.branchIds(), f.teamId(), f.groupId(), deviceName, pageable)
                .map(r -> reportDeviceIncidentService.mapToDTO(r, new ReportDeviceIncidentDTO()));
    }

    private DashboardPlanDetailRowDTO mapPlanDetailRow(final PlanDetail pd) {
        final DashboardPlanDetailRowDTO row = new DashboardPlanDetailRowDTO();
        row.setPlanDetailId(pd.getId());
        row.setPlanDetailStatus(pd.getStatus());
        row.setEstimatedTime(pd.getEstimatedTime());
        if (pd.getPlan() != null) {
            row.setPlanId(pd.getPlan().getId());
            row.setPlanName(pd.getPlan().getName());
        }
        final Device device = pd.getDevice();
        if (device != null) {
            row.setDeviceId(device.getId());
            row.setDeviceCode(device.getCode());
            row.setDeviceName(device.getName());
            if (device.getBranch() != null) {
                row.setBranchName(device.getBranch().getName());
            }
            if (device.getTeam() != null) {
                row.setTeamName(device.getTeam().getName());
            }
            if (device.getLine() != null) {
                row.setLineName(device.getLine().getName());
            }
        }
        return row;
    }

    private DashboardMaintenanceExecutionDTO mapMaintenanceExecution(final Object[] row) {
        final DashboardMaintenanceExecutionDTO dto = new DashboardMaintenanceExecutionDTO();
        dto.setPlanResultId(asLong(row[0]));
        dto.setPlanDetailId(asLong(row[1]));
        dto.setDeviceId(asLong(row[2]));
        dto.setDeviceCode(asString(row[3]));
        dto.setDeviceName(asString(row[4]));
        dto.setPlanName(asString(row[5]));
        if (row[6] instanceof LocalDateTime ldt) {
            dto.setDateTest(ldt);
        } else if (row[6] instanceof Timestamp ts) {
            dto.setDateTest(ts.toLocalDateTime());
        }
        dto.setResultStatus(row[7] != null ? ((Number) row[7]).intValue() : null);
        dto.setBranchName(asString(row[8]));
        dto.setTeamName(asString(row[9]));
        dto.setLineName(asString(row[10]));
        return dto;
    }

    private AcceptanceDTO mapAcceptanceBrief(final Acceptance a) {
        final AcceptanceDTO dto = new AcceptanceDTO();
        dto.setId(a.getId());
        dto.setCode(a.getCode());
        dto.setName(a.getName());
        dto.setType(a.getType());
        dto.setStatus(a.getStatus());
        dto.setTimeAcceptance(a.getTimeAcceptance());
        dto.setDevice(a.getDevice());
        return dto;
    }

    private ResolvedFilter resolveFilter(final DashboardFilter filter) {
        final YearMonth ym = YearMonth.now();
        final int year = filter != null && filter.getYear() != null ? filter.getYear() : ym.getYear();
        final int month = filter != null && filter.getMonth() != null ? filter.getMonth() : ym.getMonthValue();
        return new ResolvedFilter(
                resolveBranchIds(filter),
                filter != null ? filter.getTeamId() : null,
                filter != null ? filter.getGroupId() : null,
                year,
                month);
    }

    /**
     * Gop {@code branchIds}, {@code branchId}, {@code branchNames}, {@code branchName}.
     * Khong co bat ky thong tin ngành nao → null (tong toan he thong).
     * Co loc nhung khong map duoc id hop le → [-1] (so lieu = 0).
     */
    private List<Long> resolveBranchIds(final DashboardFilter filter) {
        if (filter == null) {
            return null;
        }
        final boolean branchFilterRequested = hasBranchFilter(filter);
        final Set<Long> ids = new LinkedHashSet<>();
        if (filter.getBranchIds() != null) {
            filter.getBranchIds().stream().filter(Objects::nonNull).forEach(ids::add);
        }
        if (filter.getBranchId() != null) {
            ids.add(filter.getBranchId());
        }
        if (filter.getBranchNames() != null) {
            for (final String raw : filter.getBranchNames()) {
                final String name = clean(raw);
                if (name != null) {
                    branchRepository.findFirstByNameIgnoreCase(name).map(Branch::getId).ifPresent(ids::add);
                }
            }
        }
        final String branchName = clean(filter.getBranchName());
        if (branchName != null) {
            branchRepository.findFirstByNameIgnoreCase(branchName).map(Branch::getId).ifPresent(ids::add);
        }
        if (ids.isEmpty()) {
            return branchFilterRequested ? List.of(-1L) : null;
        }
        return new ArrayList<>(ids);
    }

    private static boolean hasBranchFilter(final DashboardFilter filter) {
        if (filter.getBranchId() != null) {
            return true;
        }
        if (clean(filter.getBranchName()) != null) {
            return true;
        }
        if (filter.getBranchIds() != null && !filter.getBranchIds().isEmpty()) {
            return true;
        }
        if (filter.getBranchNames() != null) {
            for (final String raw : filter.getBranchNames()) {
                if (clean(raw) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String normalizeDueType(final String type) {
        if (type == null || type.isBlank()) {
            return "OVERDUE";
        }
        final String upper = type.trim().toUpperCase();
        if (Objects.equals(upper, "UPCOMING") || Objects.equals(upper, "ALL")) {
            return upper;
        }
        return "OVERDUE";
    }

    private static String clean(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private static Long asLong(final Object obj) {
        return obj != null ? ((Number) obj).longValue() : null;
    }

    private static String asString(final Object obj) {
        return obj != null ? obj.toString() : null;
    }

    private record ResolvedFilter(List<Long> branchIds, Long teamId, Long groupId, int year, int month) {}
}