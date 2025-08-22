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
import rd.project.qltb.model.PlanSupplieDTO;
import rd.project.qltb.service.PlanSupplieService;


@RestController
@RequestMapping(value = "/api/planSupplies", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanSupplieResource {

    private final PlanSupplieService planSupplieService;

    public PlanSupplieResource(final PlanSupplieService planSupplieService) {
        this.planSupplieService = planSupplieService;
    }

    @GetMapping
    public ResponseEntity<List<PlanSupplieDTO>> getAllPlanSupplies() {
        return ResponseEntity.ok(planSupplieService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanSupplieDTO> getPlanSupplie(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planSupplieService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlanSupplie(
            @RequestBody @Valid final PlanSupplieDTO planSupplieDTO) {
        final Long createdId = planSupplieService.create(planSupplieDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlanSupplie(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PlanSupplieDTO planSupplieDTO) {
        planSupplieService.update(id, planSupplieDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlanSupplie(@PathVariable(name = "id") final Long id) {
        planSupplieService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
