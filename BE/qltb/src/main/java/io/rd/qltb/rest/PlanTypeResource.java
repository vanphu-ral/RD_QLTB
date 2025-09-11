package io.rd.qltb.rest;

import io.rd.qltb.model.PlanTypeDTO;
import io.rd.qltb.service.PlanTypeService;
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
@RequestMapping(value = "/api/planTypes", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanTypeResource {

    private final PlanTypeService planTypeService;

    public PlanTypeResource(final PlanTypeService planTypeService) {
        this.planTypeService = planTypeService;
    }

    @GetMapping
    public ResponseEntity<List<PlanTypeDTO>> getAllPlanTypes() {
        return ResponseEntity.ok(planTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanTypeDTO> getPlanType(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planTypeService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlanType(
            @RequestBody @Valid final PlanTypeDTO planTypeDTO) {
        final Long createdId = planTypeService.create(planTypeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlanType(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PlanTypeDTO planTypeDTO) {
        planTypeService.update(id, planTypeDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlanType(@PathVariable(name = "id") final Long id) {
        planTypeService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
