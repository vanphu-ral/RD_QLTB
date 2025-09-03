package io.qltb.qltb.rest;

import io.qltb.qltb.model.PlanDTO;
import io.qltb.qltb.service.PlanService;
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


@RestController
@RequestMapping(value = "/api/plans", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanResource {

    private final PlanService planService;

    public PlanResource(final PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public ResponseEntity<List<PlanDTO>> getAllPlans() {
        return ResponseEntity.ok(planService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlan(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlan(@RequestBody @Valid final PlanDTO planDTO) {
        final Long createdId = planService.create(planDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlan(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PlanDTO planDTO) {
        planService.update(id, planDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlan(@PathVariable(name = "id") final Long id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
