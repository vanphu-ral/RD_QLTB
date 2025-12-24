package io.rd.qltb.rest;

import io.rd.qltb.model.PlanSupplieDetailDTO;
import io.rd.qltb.service.PlanSupplieDetailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/planSupplieDetails", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanSupplieDetailResource {

    private final PlanSupplieDetailService planSupplieDetailService;

    public PlanSupplieDetailResource(final PlanSupplieDetailService planSupplieDetailService) {
        this.planSupplieDetailService = planSupplieDetailService;
    }

    @GetMapping
    public ResponseEntity<List<PlanSupplieDetailDTO>> getAllPlanSupplieDetails() {
        return ResponseEntity.ok(planSupplieDetailService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanSupplieDetailDTO> getPlanSupplieDetail(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planSupplieDetailService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createPlanSupplieDetail(
            @RequestBody @Valid final PlanSupplieDetailDTO planSupplieDetailDTO) {
        final Long createdId = planSupplieDetailService.create(planSupplieDetailDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlanSupplieDetail(@PathVariable(name = "id") final Long id,
                                                        @RequestBody @Valid final PlanSupplieDetailDTO planSupplieDetailDTO) {
        planSupplieDetailService.update(id, planSupplieDetailDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanSupplieDetail(@PathVariable(name = "id") final Long id) {
        planSupplieDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
