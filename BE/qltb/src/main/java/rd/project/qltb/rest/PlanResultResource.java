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
import rd.project.qltb.model.PlanResultDTO;
import rd.project.qltb.service.PlanResultService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/planResults", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanResultResource {

    private final PlanResultService planResultService;

    public PlanResultResource(final PlanResultService planResultService) {
        this.planResultService = planResultService;
    }

    @GetMapping
    public ResponseEntity<List<PlanResultDTO>> getAllPlanResults() {
        return ResponseEntity.ok(planResultService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResultDTO> getPlanResult(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planResultService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlanResult(
            @RequestBody @Valid final PlanResultDTO planResultDTO) {
        final Long createdId = planResultService.create(planResultDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlanResult(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PlanResultDTO planResultDTO) {
        planResultService.update(id, planResultDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlanResult(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = planResultService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        planResultService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
