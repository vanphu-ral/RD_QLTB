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
import rd.project.qltb.model.DepreciationManagementDTO;
import rd.project.qltb.service.DepreciationManagementService;


@RestController
@RequestMapping(value = "/api/depreciationManagements", produces = MediaType.APPLICATION_JSON_VALUE)
public class DepreciationManagementResource {

    private final DepreciationManagementService depreciationManagementService;

    public DepreciationManagementResource(
            final DepreciationManagementService depreciationManagementService) {
        this.depreciationManagementService = depreciationManagementService;
    }

    @GetMapping
    public ResponseEntity<List<DepreciationManagementDTO>> getAllDepreciationManagements() {
        return ResponseEntity.ok(depreciationManagementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepreciationManagementDTO> getDepreciationManagement(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(depreciationManagementService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDepreciationManagement(
            @RequestBody @Valid final DepreciationManagementDTO depreciationManagementDTO) {
        final Long createdId = depreciationManagementService.create(depreciationManagementDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDepreciationManagement(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DepreciationManagementDTO depreciationManagementDTO) {
        depreciationManagementService.update(id, depreciationManagementDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDepreciationManagement(
            @PathVariable(name = "id") final Long id) {
        depreciationManagementService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
