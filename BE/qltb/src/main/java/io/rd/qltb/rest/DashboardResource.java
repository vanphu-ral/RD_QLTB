package io.rd.qltb.rest;

import io.rd.qltb.model.AcceptanceDTO;
import io.rd.qltb.model.DashboardDTO;
import io.rd.qltb.model.DashboardFilter;
import io.rd.qltb.model.DeviceDTO;
import io.rd.qltb.model.ReportDeviceIncidentDTO;
import io.rd.qltb.model.response.DashboardMaintenanceExecutionDTO;
import io.rd.qltb.model.response.DashboardPlanDetailRowDTO;
import io.rd.qltb.model.response.DeviceMaintenanceDTO;
import io.rd.qltb.service.DashboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dashboard REST: một API tổng hợp số liệu và mỗi chỉ số một API chi tiết (phân trang Spring {@link Pageable}).
 */
@RestController
@RequestMapping(value = "/api/dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
public class DashboardResource {

    private final DashboardService dashboardService;

    public DashboardResource(final DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Tổng hợp KPI. Query: year, month, branchIds, branchNames, branchId, branchName, teamId, groupId, includeTiming.
     * Không truyền year/month → dùng năm và tháng hiện tại (server).
     */
  /**
     * GET: uu tien query param (?branchName=...&year=...).
     * Ho tro them body JSON (Postman) — {@code @ModelAttribute} khong doc body GET.
     */
    @GetMapping(value = "/data", consumes = { MediaType.ALL_VALUE, MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<DashboardDTO> getDashboardSummaryGet(
            @ModelAttribute final DashboardFilter queryFilter,
            @RequestBody(required = false) final DashboardFilter bodyFilter) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(mergeDashboardFilter(queryFilter, bodyFilter)));
    }

    @PostMapping("/data")
    public ResponseEntity<DashboardDTO> getDashboardSummary(@RequestBody(required = false) final DashboardFilter filter) {
        return ResponseEntity.ok(dashboardService.getDashboardSummary(filter));
    }

    /** Mục 1 — thiết bị đang hoạt động (state=1). */
    @PostMapping("/working-devices")
    public ResponseEntity<Page<DeviceDTO>> getWorkingDevices(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getWorkingDevices(filter, pageable));
    }

    /** Mục 2 — chi tiết KH bảo trì tháng (MAINTENANCE). */
    @PostMapping("/monthly-maintenance-plan")
    public ResponseEntity<Page<DashboardPlanDetailRowDTO>> getMonthlyMaintenancePlan(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getMonthlyMaintenancePlanDetails(filter, pageable));
    }

    /** Thiết bị trong KH bảo trì năm. */
    @PostMapping("/yearly-maintenance-plan")
    public ResponseEntity<Page<DashboardPlanDetailRowDTO>> getYearlyMaintenancePlan(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getYearlyMaintenancePlanDetails(filter, pageable));
    }

    /** Thiết bị đã hoàn thành bảo dưỡng (plan_detail status=5). */
    @PostMapping("/maintained-devices")
    public ResponseEntity<Page<DashboardPlanDetailRowDTO>> getMaintainedDevices(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getMaintainedPlanDetails(filter, pageable));
    }

    /** Mục 3 — lượt thực hiện bảo trì. */
    @PostMapping("/maintenance-executions")
    public ResponseEntity<Page<DashboardMaintenanceExecutionDTO>> getMaintenanceExecutions(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getMaintenanceExecutions(filter, pageable));
    }

    /** Mục 5 — nghiệm thu SC/BĐ định kỳ. */
    @PostMapping("/accepted-maintenance")
    public ResponseEntity<Page<AcceptanceDTO>> getAcceptedMaintenance(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getAcceptedMaintenanceDetails(filter, pageable));
    }

    /**
     * Mục 5 — đến hạn / quá hạn bảo dưỡng.
     * Filter {@code maintenanceDueType}: OVERDUE | UPCOMING | ALL.
     */
    @PostMapping("/due-maintenance")
    public ResponseEntity<Page<DeviceMaintenanceDTO>> getDueMaintenance(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getDueMaintenanceDetails(filter, pageable));
    }

    /** Mục 6 — thiết bị {@code devices.status = 3}. */
    @PostMapping("/faulty-devices")
    public ResponseEntity<Page<DeviceDTO>> getFaultyDevices(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getFaultyDevicesByStatus(filter, pageable));
    }

    /** Mục 7 — biên bản {@code report_device_incidents}. */
    @PostMapping("/critical-incidents")
    public ResponseEntity<Page<ReportDeviceIncidentDTO>> getCriticalIncidents(
            @RequestBody(required = false) final DashboardFilter filter,
            final Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getCriticalIncidentDetails(filter, pageable));
    }

    private static DashboardFilter mergeDashboardFilter(
            final DashboardFilter queryFilter, final DashboardFilter bodyFilter) {
        if (bodyFilter != null && hasFilterValues(bodyFilter)) {
            return bodyFilter;
        }
        if (queryFilter != null && hasFilterValues(queryFilter)) {
            return queryFilter;
        }
        return bodyFilter != null ? bodyFilter : queryFilter;
    }

    private static boolean hasFilterValues(final DashboardFilter filter) {
        if (filter == null) {
            return false;
        }
        if (filter.getBranchId() != null) {
            return true;
        }
        if (filter.getBranchIds() != null && !filter.getBranchIds().isEmpty()) {
            return true;
        }
        if (filter.getBranchName() != null && !filter.getBranchName().isBlank()) {
            return true;
        }
        if (filter.getBranchNames() != null && !filter.getBranchNames().isEmpty()) {
            return true;
        }
        if (filter.getTeamId() != null || filter.getGroupId() != null) {
            return true;
        }
        if (filter.getYear() != null || filter.getMonth() != null) {
            return true;
        }
        if (filter.getDeviceName() != null && !filter.getDeviceName().isBlank()) {
            return true;
        }
        if (filter.getMaintenanceDueType() != null && !filter.getMaintenanceDueType().isBlank()) {
            return true;
        }
        return Boolean.TRUE.equals(filter.getIncludeTiming());
    }
}