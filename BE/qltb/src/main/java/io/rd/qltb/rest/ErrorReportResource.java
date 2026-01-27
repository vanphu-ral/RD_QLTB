package io.rd.qltb.rest;

import io.rd.qltb.model.ErrorReportDTO;
import io.rd.qltb.service.ErrorReportService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/errorReports", produces = MediaType.APPLICATION_JSON_VALUE)
public class ErrorReportResource {

    private final ErrorReportService errorReportService;

    public ErrorReportResource(final ErrorReportService errorReportService) {
        this.errorReportService = errorReportService;
    }

    @GetMapping
    public ResponseEntity<List<ErrorReportDTO>> getAllErrorReports() {
        return ResponseEntity.ok(errorReportService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ErrorReportDTO> getErrorReport(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(errorReportService.get(id));
    }
    @GetMapping("/plan-detail/{id}")
    public ResponseEntity<List<ErrorReportDTO>> findByPlanDetailId(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(errorReportService.findByPlanDetailId(id));
    }
    @GetMapping("/plan-detail/scan-qr/{id}")
    public ResponseEntity<List<ErrorReportDTO>> findByPlanResultId(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(errorReportService.findByPlanResultId(id));
    }
    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createErrorReport(
            @RequestBody @Valid final ErrorReportDTO errorReportDTO) {
        final Long createdId = errorReportService.create(errorReportDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateErrorReport(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ErrorReportDTO errorReportDTO) {
        errorReportService.update(id, errorReportDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteErrorReport(@PathVariable(name = "id") final Long id) {
        errorReportService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list-error-by-device")
    public ResponseEntity<Page<ErrorReportDTO>> getDetailsByDevice(
            @RequestParam final Long deviceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime toDate,
            final Pageable pageable) {
        return ResponseEntity.ok(errorReportService.getErrorDetails(deviceId, fromDate, toDate, pageable));
    }

}
