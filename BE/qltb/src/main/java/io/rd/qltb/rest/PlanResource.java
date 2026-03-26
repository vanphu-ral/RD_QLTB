package io.rd.qltb.rest;

import io.rd.qltb.model.*;
import io.rd.qltb.model.response.PlanUpdateResponse;
import io.rd.qltb.service.PlanService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;


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
    @GetMapping("/paged")
    public ResponseEntity<Page<PlanDTO>> getPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam Map<String, String> params) {

        // Loại bỏ param "page" khỏi filters
        params.remove("page");

        // Chuyển sang Map<String,Object> để truyền xuống service
        Map<String, Object> filters = new HashMap<>(params);

        Page<PlanDTO> result = planService.findPlansPaged(filters, page);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDTO> getPlan(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planService.get(id));
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<PlanRequest> getPlanDetail(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planService.getPlanDetail(id));
    }

//    @PostMapping
//    @ApiResponse(responseCode = "201")
//    public ResponseEntity<Long> createPlan(@RequestBody @Valid final PlanDTO planDTO) {
//        final Long createdId = planService.create(planDTO);
//        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
//    }
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
//    @PutMapping("/{id}")
//    public ResponseEntity<Long> updatePlan(@PathVariable(name = "id") final Long id,
//            @RequestBody @Valid final PlanDTO planDTO) {
//        planService.update(id, planDTO);
//        return ResponseEntity.ok(id);
//    }

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
    @GetMapping("/details/daily/{id}")
    public ResponseEntity<PlanDTO> getPlanWithDetailsDaily(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planService.getDailyById(id));
    }
    @GetMapping("/details/maintain/{id}")
    public ResponseEntity<PlanDTO> getPlanWithDetailsMaintain(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(planService.getMaintainById(id));
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer value) {
        planService.updateStatus(id, value);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody PlanRequest request,
    @AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok(planService.createPlan(request,oidcUser.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @AuthenticationPrincipal OidcUser oidcUser,
                                    @RequestBody PlanRequest request) {
        PlanUpdateResponse planUpdateResponse = planService.updatePlan(id, oidcUser.getName(), request);
        if(planUpdateResponse.getStatus().equals("FAIL")){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(planUpdateResponse);
        }else {
            return ResponseEntity.ok(planUpdateResponse);
        }
    }
}
