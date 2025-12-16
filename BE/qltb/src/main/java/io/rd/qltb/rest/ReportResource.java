package io.rd.qltb.rest;

import io.rd.qltb.model.ReportFilter;
import io.rd.qltb.model.response.ReportResponse;
import io.rd.qltb.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
}
