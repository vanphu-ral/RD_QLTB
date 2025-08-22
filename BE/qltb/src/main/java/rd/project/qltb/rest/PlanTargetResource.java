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
import rd.project.qltb.model.PlanTargetDTO;
import rd.project.qltb.service.PlanTargetService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/planTargets", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanTargetResource {

    private final PlanTargetService planTargetService;

    public PlanTargetResource(final PlanTargetService planTargetService) {
        this.planTargetService = planTargetService;
    }

    @GetMapping
    public ResponseEntity<List<PlanTargetDTO>> getAllPlanTargets() {
        return ResponseEntity.ok(planTargetService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanTargetDTO> getPlanTarget(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planTargetService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlanTarget(
            @RequestBody @Valid final PlanTargetDTO planTargetDTO) {
        final Long createdId = planTargetService.create(planTargetDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlanTarget(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PlanTargetDTO planTargetDTO) {
        planTargetService.update(id, planTargetDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlanTarget(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = planTargetService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        planTargetService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
