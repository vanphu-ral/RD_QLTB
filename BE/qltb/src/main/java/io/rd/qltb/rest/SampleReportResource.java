package io.rd.qltb.rest;

import io.rd.qltb.model.SampleReportDTO;
import io.rd.qltb.service.SampleReportService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/sampleReports", produces = MediaType.APPLICATION_JSON_VALUE)
public class SampleReportResource {

    private final SampleReportService sampleReportService;

    public SampleReportResource(final SampleReportService sampleReportService) {
        this.sampleReportService = sampleReportService;
    }

    @GetMapping
    public ResponseEntity<List<SampleReportDTO>> getAllSampleReports() {
        return ResponseEntity.ok(sampleReportService.findAll());
    }
    @GetMapping("/approve")
    public ResponseEntity<List<SampleReportDTO>> getAllSampleReportsByApprove() {
        return ResponseEntity.ok(sampleReportService.findAllByApprove());
    }
    @GetMapping("/{id}")
    public ResponseEntity<SampleReportDTO> getSampleReport(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(sampleReportService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSampleReport(
            @RequestBody @Valid final SampleReportDTO sampleReportDTO) {
        final Long createdId = sampleReportService.create(sampleReportDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSampleReport(@PathVariable(name = "id") final Long id,
                                                   @RequestBody @Valid final SampleReportDTO sampleReportDTO,
                                                   @AuthenticationPrincipal OidcUser oidcUser) {
        sampleReportService.update(id, sampleReportDTO,oidcUser.getName());
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSampleReport(@PathVariable(name = "id") final Long id) {
        sampleReportService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
