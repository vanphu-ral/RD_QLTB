package io.rd.qltb.rest;

import io.rd.qltb.domain.Plan;
import io.rd.qltb.model.*;
import io.rd.qltb.service.PlanDetailService;
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
@RequestMapping(value = "/api/planDetails", produces = MediaType.APPLICATION_JSON_VALUE)
public class PlanDetailResource {

    private final PlanDetailService planDetailService;

    public PlanDetailResource(final PlanDetailService planDetailService) {
        this.planDetailService = planDetailService;
    }

    @GetMapping("/daily-check-devices")
    public ResponseEntity<List<PlanDetailDTO>> getDailyCheckDevices(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String team,
            @RequestParam(required = false) String line) {
        return ResponseEntity.ok(planDetailService.getDailyCheckDevices(branch, team, line));
    }

    @GetMapping("/daily-check-devices/paged")
    public ResponseEntity<Page<PlanDetailDTO>> getDailyCheckDevicesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Map<String, String> params) {

        params.remove("page");
        params.remove("size");
        Map<String, Object> filters = new HashMap<>(params);

        Page<PlanDetailDTO> result = planDetailService.findDailyCheckDevicesPaged(filters, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/daily-check-devices/all")
    public ResponseEntity<List<PlanDetailDTO>> getDailyCheckDevicesAll(
            @RequestParam Map<String, String> params) {

        params.remove("page");
        params.remove("size");
        Map<String, Object> filters = new HashMap<>(params);

        List<PlanDetailDTO> result = planDetailService.findDailyCheckDevicesAll(filters);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/summary/{id}/monthly")
    public ResponseEntity<PlanCheckDTO> getPlanCheckDetailByMonth(
            @PathVariable("id") Long id,
            @RequestParam("entityType") String entityType,
            @RequestParam("month") int month,
            @RequestParam("year") int year) {
        PlanCheckDTO planCheckDTO = planDetailService.getPlanCheckDetailByMonth(id, entityType, month, year);
        return ResponseEntity.ok(planCheckDTO);
    }
    @GetMapping("/result/{planId}")
            public ResponseEntity<List<PlanDetailDTO>> getPlanDetailsByPlanId(@PathVariable("planId") Long planId) {
        List<PlanDetailDTO> planDetails = planDetailService.getPlanDetailsByPlanId(planId);
        return ResponseEntity.ok(planDetails);
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
    
    @GetMapping("/summary/device/{deviceId}")
    public ResponseEntity<PlanCheckDTO> getPlanCheckDetailByDeviceId(
            @PathVariable("deviceId") Long deviceId,
            @RequestParam("entityType") String entityType) {
        PlanCheckDTO planCheckDTO = planDetailService.getPlanCheckDetailByDeviceId(deviceId, entityType);
        return ResponseEntity.ok(planCheckDTO);
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
    @GetMapping("/plans")
    public ResponseEntity<List<PlanDTO>> getDeviceSupplyUsages(
            @RequestParam("qrCode") String qrCode) {
        List<PlanDTO> deviceSupplyUsages = planDetailService.getByDetiveId( qrCode);
        return ResponseEntity.ok(deviceSupplyUsages);
    }
    @GetMapping("/plan/{id}")
    public ResponseEntity<List<PlanDetailDTO>> getByPlanId(
            @PathVariable("id") Long id) {
        List<PlanDetailDTO> planDetailDTOS = planDetailService.getByPlanId(id);
        return ResponseEntity.ok(planDetailDTOS);
    }
}
