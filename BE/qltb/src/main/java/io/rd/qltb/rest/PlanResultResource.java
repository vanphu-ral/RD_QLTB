package io.rd.qltb.rest;

import io.rd.qltb.model.PlanCheckDTO;
import io.rd.qltb.model.PlanResultDTO;
import io.rd.qltb.service.PlanResultService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import static io.rd.qltb.config.ConstantStatusGlobal.COMPLETED;


@RestController
@RequestMapping(value = "/api/planResults", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanResultResource {

    private final PlanResultService planResultService;

    public PlanResultResource(final PlanResultService planResultService) {
        this.planResultService = planResultService;
    }

    @GetMapping
    public ResponseEntity<List<PlanResultDTO>> getAllPlanResults() {
        return ResponseEntity.ok(planResultService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResultDTO> getPlanResult(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planResultService.get(id));
    }
    @GetMapping("/plan-result/{id}")
    public ResponseEntity<PlanCheckDTO> getDetail(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planResultService.getDetail(id));
    }
    @PostMapping("/update-status/{id}")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> updateStatus(
            @AuthenticationPrincipal OidcUser oidcUser,
            @PathVariable Long id,
            @RequestParam Integer status) { // Thêm RequestParam để lấy status từ URL

        planResultService.updateStatus(id, status, oidcUser.getName());
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }
    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlanResult(
            @RequestBody @Valid final PlanResultDTO planResultDTO) {
        final Long createdId = planResultService.create(planResultDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }
    @PostMapping("/create-update")
    @ApiResponse(responseCode = "201")
    public ResponseEntity createUpdate(@AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody @Valid final PlanCheckDTO planCheckDTO) {
        planResultService.createUpdate(planCheckDTO,oidcUser.getName());
        return new ResponseEntity<>( HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePlanResult(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PlanResultDTO planResultDTO) {
        planResultService.update(id, planResultDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePlanResult(@PathVariable(name = "id") final Long id) {
        planResultService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/delete-all/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAll(@PathVariable(name = "id") final Long id) {
        planResultService.deleteAll(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/plan-detail/{planDetailId}")
    public ResponseEntity<List<PlanResultDTO>> getByPlanDetailId(@PathVariable Long planDetailId) {
        List<PlanResultDTO> results = planResultService.findAllByPlanDetailId(planDetailId);
        return ResponseEntity.ok(results);
    }

}
