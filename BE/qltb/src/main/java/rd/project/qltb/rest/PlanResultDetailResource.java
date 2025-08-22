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
import rd.project.qltb.model.PlanResultDetailDTO;
import rd.project.qltb.service.PlanResultDetailService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/planResultDetails", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanResultDetailResource {

    private final PlanResultDetailService planResultDetailService;

    public PlanResultDetailResource(final PlanResultDetailService planResultDetailService) {
        this.planResultDetailService = planResultDetailService;
    }

    @GetMapping
    public ResponseEntity<List<PlanResultDetailDTO>> getAllPlanResultDetails() {
        return ResponseEntity.ok(planResultDetailService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResultDetailDTO> getPlanResultDetail(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planResultDetailService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlanResultDetail(
            @RequestBody @Valid final PlanResultDetailDTO planResultDetailDTO) {
        final Long createdId = planResultDetailService.create(planResultDetailDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlanResultDetail(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PlanResultDetailDTO planResultDetailDTO) {
        planResultDetailService.update(id, planResultDetailDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlanResultDetail(@PathVariable(name = "id") final Long id) {
        final ReferencedWarning referencedWarning = planResultDetailService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        planResultDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
