package io.qltb.qltb.rest;

import io.qltb.qltb.model.KeyMappingDeviceSampleReportDTO;
import io.qltb.qltb.service.KeyMappingDeviceSampleReportService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/keyMappingDeviceSampleReports", produces = MediaType.APPLICATION_JSON_VALUE)
public class KeyMappingDeviceSampleReportResource {

    private final KeyMappingDeviceSampleReportService keyMappingDeviceSampleReportService;

    public KeyMappingDeviceSampleReportResource(
            final KeyMappingDeviceSampleReportService keyMappingDeviceSampleReportService) {
        this.keyMappingDeviceSampleReportService = keyMappingDeviceSampleReportService;
    }

    @GetMapping
    public ResponseEntity<List<KeyMappingDeviceSampleReportDTO>> getAllKeyMappingDeviceSampleReports(
            ) {
        return ResponseEntity.ok(keyMappingDeviceSampleReportService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KeyMappingDeviceSampleReportDTO> getKeyMappingDeviceSampleReport(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(keyMappingDeviceSampleReportService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createKeyMappingDeviceSampleReport(
            @RequestBody @Valid final KeyMappingDeviceSampleReportDTO keyMappingDeviceSampleReportDTO) {
        final Long createdId = keyMappingDeviceSampleReportService.create(keyMappingDeviceSampleReportDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateKeyMappingDeviceSampleReport(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final KeyMappingDeviceSampleReportDTO keyMappingDeviceSampleReportDTO) {
        keyMappingDeviceSampleReportService.update(id, keyMappingDeviceSampleReportDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteKeyMappingDeviceSampleReport(
            @PathVariable(name = "id") final Long id) {
        keyMappingDeviceSampleReportService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
