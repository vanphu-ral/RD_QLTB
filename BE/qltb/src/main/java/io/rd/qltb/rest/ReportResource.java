package io.rd.qltb.rest;

import io.rd.qltb.model.ReportFilter;
import io.rd.qltb.model.response.Report2Response;
import io.rd.qltb.model.response.ReportResponse;
import io.rd.qltb.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/reports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportResource {
    @Autowired
    private ReportService reportService;
    @PostMapping
    public List<ReportResponse> getReports(
            @RequestBody ReportFilter filter
    ) {
        return reportService.getSupplyReport(filter);
    }
    /** * API lấy báo cáo bảo trì theo filter và phân trang */
    @PostMapping("/maintenance")
    public ResponseEntity<Page<Report2Response>> getMaintenanceReport(
            @RequestBody ReportFilter filter, Pageable pageable) {
        Page<Report2Response> page = reportService.getMaintenanceReport(filter, pageable);
        return ResponseEntity.ok(page);
    }
}
