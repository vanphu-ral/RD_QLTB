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
import rd.project.qltb.model.PlanTargetResultDTO;
import rd.project.qltb.service.PlanTargetResultService;


@RestController
@RequestMapping(value = "/api/planTargetResults", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanTargetResultResource {

    private final PlanTargetResultService planTargetResultService;

    public PlanTargetResultResource(final PlanTargetResultService planTargetResultService) {
        this.planTargetResultService = planTargetResultService;
    }

    @GetMapping
    public ResponseEntity<List<PlanTargetResultDTO>> getAllPlanTargetResults() {
        return ResponseEntity.ok(planTargetResultService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanTargetResultDTO> getPlanTargetResult(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(planTargetResultService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createPlanTargetResult(
            @RequestBody @Valid final PlanTargetResultDTO planTargetResultDTO) {
        final Integer createdId = planTargetResultService.create(planTargetResultDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updatePlanTargetResult(
            @PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final PlanTargetResultDTO planTargetResultDTO) {
        planTargetResultService.update(id, planTargetResultDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlanTargetResult(
            @PathVariable(name = "id") final Integer id) {
        planTargetResultService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
