package io.rd.qltb.rest;

import io.rd.qltb.model.*;
import io.rd.qltb.service.PlanService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
    @GetMapping("/detail/{id}")
    public ResponseEntity<PlanRequest> getPlanDetail(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planService.getPlanDetail(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPlan(@RequestBody @Valid final PlanDTO planDTO) {
        final Long createdId = planService.create(planDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }
    @PostMapping("/create")
    @ApiResponse(responseCode = "201")
    public void createPlanWithDetails(
                                                                 @AuthenticationPrincipal OidcUser oidcUser,
                                                                 @RequestBody  @Valid final PlanRequest planRequest) {
        System.out.println("User Info: " + oidcUser.getName());
         planService.createPlanWithDetails(planRequest,oidcUser.getName());
//        return new ResponseEntity<>(planWithDetails, HttpStatus.CREATED);
    }
    @PostMapping("/create-2")
    @ApiResponse(responseCode = "201")
    public void createPlanWithDetails2(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody  @Valid final PlanRequest2 planRequest) {
        System.out.println("User Info: " + oidcUser.getName());
        planService.createPlan2(planRequest);
//        return new ResponseEntity<>(planWithDetails, HttpStatus.CREATED);
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
    @DeleteMapping("/delete/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteByPlanId(@PathVariable(name = "id") final Long id) {
        planService.deleteByPlanId(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/with-details")
    public ResponseEntity<List<PlanWithDetailsDTO>> getAllPlansWithDetails() {
        return ResponseEntity.ok(planService.findAllWithDetails());
    }
}
