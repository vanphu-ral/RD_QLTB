package io.rd.qltb.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rd.qltb.model.AcceptanceDTO;
import io.rd.qltb.model.DashboardDTO;
import io.rd.qltb.model.DashboardFilter;
import io.rd.qltb.model.DeviceDTO;
import io.rd.qltb.model.ReportDeviceIncidentDTO;
import io.rd.qltb.model.response.DashboardMaintenanceExecutionDTO;
import io.rd.qltb.model.response.DashboardPlanDetailRowDTO;
import io.rd.qltb.model.response.DeviceMaintenanceDTO;
import io.rd.qltb.service.DashboardService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class DashboardResourceTest {

    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardResource dashboardResource;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Pageable testPageable = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardResource)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @Test
    @DisplayName("POST /api/dashboard/data - Trả về số liệu tổng hợp 10 thẻ KPI")
    void testGetDashboardSummaryPost() throws Exception {
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalDevices(100);
        dto.setTotalWorkingDevices(85);
        dto.setTotalDevicesInMaintenance(15);
        dto.setTotalDevicesMaintained(30);
        dto.setTotalMaintained(25);
        dto.setMaintenanceExecutionCount(40);
        dto.setAcceptedPeriodicMaintenanceDeviceCount(20);
        dto.setDueMaintenanceDeviceCount(10);
        dto.setFaultyDeviceCount(5);
        dto.setCriticalIncidentReportCount(2);

        when(dashboardService.getDashboardSummary(any())).thenReturn(dto);

        DashboardFilter filter = new DashboardFilter();
        filter.setYear(2026);
        filter.setMonth(11);

        mockMvc.perform(post("/api/dashboard/data")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDevices").value(100))
                .andExpect(jsonPath("$.totalWorkingDevices").value(85))
                .andExpect(jsonPath("$.totalDevicesInMaintenance").value(15))
                .andExpect(jsonPath("$.totalDevicesMaintained").value(30))
                .andExpect(jsonPath("$.totalMaintained").value(25))
                .andExpect(jsonPath("$.maintenanceExecutionCount").value(40))
                .andExpect(jsonPath("$.acceptedPeriodicMaintenanceDeviceCount").value(20))
                .andExpect(jsonPath("$.dueMaintenanceDeviceCount").value(10))
                .andExpect(jsonPath("$.faultyDeviceCount").value(5))
                .andExpect(jsonPath("$.criticalIncidentReportCount").value(2));
    }

    @Test
    @DisplayName("GET /api/dashboard/data - Trả về số liệu tổng hợp qua query param")
    void testGetDashboardSummaryGet() throws Exception {
        DashboardDTO dto = new DashboardDTO();
        dto.setTotalWorkingDevices(50);
        when(dashboardService.getDashboardSummary(any())).thenReturn(dto);

        mockMvc.perform(get("/api/dashboard/data?year=2026&month=11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalWorkingDevices").value(50));
    }

    @Test
    @DisplayName("POST /api/dashboard/working-devices - Danh sách thiết bị đang hoạt động")
    void testGetWorkingDevices() throws Exception {
        DeviceDTO device = new DeviceDTO();
        device.setId(1L);
        device.setCode("TB-001");
        device.setName("Máy dán chip");

        when(dashboardService.getWorkingDevices(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(device), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/working-devices?page=0&size=10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("TB-001"))
                .andExpect(jsonPath("$.content[0].name").value("Máy dán chip"));
    }

    @Test
    @DisplayName("POST /api/dashboard/monthly-maintenance-plan - Kế hoạch bảo trì tháng")
    void testGetMonthlyMaintenancePlan() throws Exception {
        DashboardPlanDetailRowDTO row = new DashboardPlanDetailRowDTO();
        row.setPlanDetailId(10L);
        row.setDeviceCode("TB-M01");
        row.setPlanName("Bảo trì tháng 11");

        when(dashboardService.getMonthlyMaintenancePlanDetails(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/monthly-maintenance-plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].deviceCode").value("TB-M01"))
                .andExpect(jsonPath("$.content[0].planName").value("Bảo trì tháng 11"));
    }

    @Test
    @DisplayName("POST /api/dashboard/yearly-maintenance-plan - Kế hoạch bảo trì năm")
    void testGetYearlyMaintenancePlan() throws Exception {
        DashboardPlanDetailRowDTO row = new DashboardPlanDetailRowDTO();
        row.setPlanDetailId(20L);
        row.setDeviceCode("TB-Y01");

        when(dashboardService.getYearlyMaintenancePlanDetails(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/yearly-maintenance-plan")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].deviceCode").value("TB-Y01"));
    }

    @Test
    @DisplayName("POST /api/dashboard/maintained-devices - Thiết bị đã hoàn thành bảo dưỡng")
    void testGetMaintainedDevices() throws Exception {
        DashboardPlanDetailRowDTO row = new DashboardPlanDetailRowDTO();
        row.setDeviceCode("TB-DONE");
        row.setPlanDetailStatus(5);

        when(dashboardService.getMaintainedPlanDetails(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/maintained-devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].deviceCode").value("TB-DONE"))
                .andExpect(jsonPath("$.content[0].planDetailStatus").value(5));
    }

    @Test
    @DisplayName("POST /api/dashboard/maintenance-executions - Lượt thực hiện bảo trì")
    void testGetMaintenanceExecutions() throws Exception {
        DashboardMaintenanceExecutionDTO exec = new DashboardMaintenanceExecutionDTO();
        exec.setPlanResultId(100L);
        exec.setDeviceCode("TB-EX");

        when(dashboardService.getMaintenanceExecutions(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(exec), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/maintenance-executions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].deviceCode").value("TB-EX"));
    }

    @Test
    @DisplayName("POST /api/dashboard/accepted-maintenance - Biên bản nghiệm thu")
    void testGetAcceptedMaintenance() throws Exception {
        AcceptanceDTO acceptance = new AcceptanceDTO();
        acceptance.setId(1L);
        acceptance.setCode("NT-2026-001");

        when(dashboardService.getAcceptedMaintenanceDetails(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(acceptance), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/accepted-maintenance")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("NT-2026-001"));
    }

    @Test
    @DisplayName("POST /api/dashboard/due-maintenance - Thiết bị đến hạn bảo dưỡng")
    void testGetDueMaintenance() throws Exception {
        DeviceMaintenanceDTO due = new DeviceMaintenanceDTO();
        due.setDeviceName("Máy ép thủy lực");
        due.setStatus("OVERDUE");

        when(dashboardService.getDueMaintenanceDetails(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(due), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/due-maintenance")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].deviceName").value("Máy ép thủy lực"))
                .andExpect(jsonPath("$.content[0].status").value("OVERDUE"));
    }

    @Test
    @DisplayName("POST /api/dashboard/faulty-devices - Thiết bị lỗi/hỏng")
    void testGetFaultyDevices() throws Exception {
        DeviceDTO faulty = new DeviceDTO();
        faulty.setCode("TB-ERR");

        when(dashboardService.getFaultyDevicesByStatus(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(faulty), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/faulty-devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("TB-ERR"));
    }

    @Test
    @DisplayName("POST /api/dashboard/critical-incidents - Sự cố nghiêm trọng")
    void testGetCriticalIncidents() throws Exception {
        ReportDeviceIncidentDTO incident = new ReportDeviceIncidentDTO();
        incident.setCode("SC-001");

        when(dashboardService.getCriticalIncidentDetails(any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(incident), testPageable, 1));

        mockMvc.perform(post("/api/dashboard/critical-incidents")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].code").value("SC-001"));
    }
}
