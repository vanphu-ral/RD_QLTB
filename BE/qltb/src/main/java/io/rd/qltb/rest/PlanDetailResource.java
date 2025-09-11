package io.rd.qltb.rest;

import io.rd.qltb.model.PlanDetailDTO;
import io.rd.qltb.service.PlanDetailService;
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
@RequestMapping(value = "/api/planDetails", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanDetailResource {

    private final PlanDetailService planDetailService;

    public PlanDetailResource(final PlanDetailService planDetailService) {
        this.planDetailService = planDetailService;
    }

    @GetMapping
    public ResponseEntity<List<PlanDetailDTO>> getAllPlanDetails() {
        return ResponseEntity.ok(planDetailService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDetailDTO> getPlanDetail(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planDetailService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlanDetail(
            @RequestBody @Valid final PlanDetailDTO planDetailDTO) {
        final Long createdId = planDetailService.create(planDetailDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlanDetail(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PlanDetailDTO planDetailDTO) {
        planDetailService.update(id, planDetailDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlanDetail(@PathVariable(name = "id") final Long id) {
        planDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
