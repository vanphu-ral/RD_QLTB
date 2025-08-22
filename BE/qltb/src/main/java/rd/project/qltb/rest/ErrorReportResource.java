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
import rd.project.qltb.model.ErrorReportDTO;
import rd.project.qltb.service.ErrorReportService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


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
        final ReferencedWarning referencedWarning = errorReportService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        errorReportService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
