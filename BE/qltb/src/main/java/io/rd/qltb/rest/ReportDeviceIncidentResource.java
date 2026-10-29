package io.rd.qltb.rest;

import io.rd.qltb.model.ReportDeviceIncidentDTO;
import io.rd.qltb.service.ReportDeviceIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/reportDeviceIncident", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReportDeviceIncidentResource {
    @Autowired
    private ReportDeviceIncidentService reportDeviceIncidentService;

    @GetMapping
    public List<ReportDeviceIncidentDTO> getAllReportDeviceIncidents() {
        return reportDeviceIncidentService.findAll();
    }

    @GetMapping("/{id}")
    public ReportDeviceIncidentDTO getReportDeviceIncident(@PathVariable final Long id) {
        return reportDeviceIncidentService.get(id);
    }

    @GetMapping("exist/error-report/{id}")
    public ResponseEntity<Map<String, Integer>> checkIncidentExistErrorReport(
            @PathVariable(name = "id") final Long id) {
        Integer exists = reportDeviceIncidentService.checkIfExistByErrorReportId(id);
        Map<String, Integer> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest-form-config")
    public ResponseEntity<Map<String, Object>> getLatestFormConfig() {
        return ResponseEntity.ok(reportDeviceIncidentService.getLatestApprovedFormConfig());
    }

    @GetMapping("/unrecorded-errors")
    public ResponseEntity<List<Map<String, Object>>> getUnrecordedErrors(
            @RequestParam(name = "branch", required = false) final String branch) {
        return ResponseEntity.ok(reportDeviceIncidentService.getUnrecordedSeriousErrors(branch));
    }

    @PostMapping
    public ResponseEntity<?> createReportDeviceIncident(@RequestBody final ReportDeviceIncidentDTO reportDeviceIncidentDTO) {
        return ResponseEntity.status(201).body(reportDeviceIncidentService.createReportDeviceIncident(reportDeviceIncidentDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReportDeviceIncident(@PathVariable final Long id, @RequestBody final ReportDeviceIncidentDTO reportDeviceIncidentDTO) {
        reportDeviceIncidentService.updateReportDeviceIncident(id, reportDeviceIncidentDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReportDeviceIncident(@PathVariable final Long id) {
        reportDeviceIncidentService.deleteReportDeviceIncident(id);
        return ResponseEntity.noContent().build();
    }
}
