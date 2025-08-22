package rd.project.qltb.rest;

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
import rd.project.qltb.model.SampleReportDTO;
import rd.project.qltb.service.SampleReportService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


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
            @RequestBody @Valid final SampleReportDTO sampleReportDTO) {
        sampleReportService.update(id, sampleReportDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSampleReport(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = sampleReportService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        sampleReportService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
