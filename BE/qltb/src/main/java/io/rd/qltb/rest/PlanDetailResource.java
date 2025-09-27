package io.rd.qltb.rest;

import io.rd.qltb.domain.Plan;
import io.rd.qltb.model.ApprovalRequestDTO;
import io.rd.qltb.model.DeviceSupplyUsageDTO;
import io.rd.qltb.model.PlanCheckDTO;
import io.rd.qltb.model.PlanDetailDTO;
import io.rd.qltb.service.PlanDetailService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;


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
    @GetMapping("/summary/{id}")
    public ResponseEntity<PlanCheckDTO> getPlanCheckDetail(
            @PathVariable("id") Long id,
            @RequestParam("entityType") String entityType) {
        PlanCheckDTO planCheckDTO = planDetailService.getPlanCheckDetail(id, entityType);
        return ResponseEntity.ok(planCheckDTO);
    }
    @GetMapping("/{id}")
    public ResponseEntity<PlanDetailDTO> getPlanDetail(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planDetailService.get(id));
    }
    @PostMapping("/approval")
    public ResponseEntity createScriptApproval(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody ApprovalRequestDTO approvalRequestDTO,
            @RequestParam("entityType") String entityType) {
         planDetailService.createScriptApproval(approvalRequestDTO, entityType,oidcUser.getName());
        return ResponseEntity.ok().build();
    }
    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlanDetail(
            @RequestBody @Valid final PlanDetailDTO planDetailDTO) {
        final Long createdId = planDetailService.create(planDetailDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }
    @PostMapping("/creates")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<List<Long>> creates(@RequestBody @Valid List<PlanDetailDTO> planDetailDTO) {
        List<Long> createdIds = planDetailService.creates(planDetailDTO);
        return new ResponseEntity<>(createdIds, HttpStatus.CREATED);
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
