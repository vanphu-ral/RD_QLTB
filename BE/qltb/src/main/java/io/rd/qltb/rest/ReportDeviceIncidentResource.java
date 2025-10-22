package io.rd.qltb.rest;

import io.rd.qltb.model.ReportDeviceIncidentDTO;
import io.rd.qltb.service.ReportDeviceIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/reportDeviceIncidentResource", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @PostMapping
    public ResponseEntity<?> createReportDeviceIncident(@RequestBody final ReportDeviceIncidentDTO reportDeviceIncidentDTO) {
        return ResponseEntity.status(201).body(reportDeviceIncidentService.createReportDeviceIncident(reportDeviceIncidentDTO));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReportDeviceIncident(@PathVariable final Long id, @RequestBody final ReportDeviceIncidentDTO reportDeviceIncidentDTO) {
        reportDeviceIncidentService.updateReportDeviceIncident(id, reportDeviceIncidentDTO);
        return ResponseEntity.ok().build();
    }
}
