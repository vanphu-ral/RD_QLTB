package io.rd.qltb.rest;

import io.rd.qltb.model.PlanResultCheckLogDTO;
import io.rd.qltb.service.PlanResultCheckLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plan-result-check-logs")
public class PlanResultCheckLogResource {
    private final PlanResultCheckLogService planResultCheckLogService;

    public PlanResultCheckLogResource(final PlanResultCheckLogService planResultCheckLogService) {
        this.planResultCheckLogService = planResultCheckLogService;
    }

    @GetMapping
    public ResponseEntity<List<PlanResultCheckLogDTO>> getAllPlanResultCheckLogs() {
        return ResponseEntity.ok(planResultCheckLogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<PlanResultCheckLogDTO>> getPlanResultCheckLog(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planResultCheckLogService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createPlanResultCheckLog(
            @RequestBody @Valid final PlanResultCheckLogDTO planResultCheckLogDTO) {
        final Long createdId = planResultCheckLogService.create(planResultCheckLogDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePlanResultCheckLog(@PathVariable(name = "id") final Long id,
                                                         @RequestBody @Valid final PlanResultCheckLogDTO planResultCheckLogDTO) {
        planResultCheckLogService.update(id, planResultCheckLogDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlanResultCheckLog(@PathVariable(name = "id") final Long id) {
        planResultCheckLogService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
