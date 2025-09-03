package io.qltb.qltb.rest;

import io.qltb.qltb.model.PerformanceManagementDTO;
import io.qltb.qltb.service.PerformanceManagementService;
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
@RequestMapping(value = "/api/performanceManagements", produces = MediaType.APPLICATION_JSON_VALUE)
public class PerformanceManagementResource {

    private final PerformanceManagementService performanceManagementService;

    public PerformanceManagementResource(
            final PerformanceManagementService performanceManagementService) {
        this.performanceManagementService = performanceManagementService;
    }

    @GetMapping
    public ResponseEntity<List<PerformanceManagementDTO>> getAllPerformanceManagements() {
        return ResponseEntity.ok(performanceManagementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceManagementDTO> getPerformanceManagement(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(performanceManagementService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPerformanceManagement(
            @RequestBody @Valid final PerformanceManagementDTO performanceManagementDTO) {
        final Long createdId = performanceManagementService.create(performanceManagementDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePerformanceManagement(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PerformanceManagementDTO performanceManagementDTO) {
        performanceManagementService.update(id, performanceManagementDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePerformanceManagement(
            @PathVariable(name = "id") final Long id) {
        performanceManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
