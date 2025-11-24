package io.rd.qltb.rest;

import io.rd.qltb.model.AcceptanceDTO;
import io.rd.qltb.service.AcceptanceService;
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
@RequestMapping(value = "/api/acceptances", produces = MediaType.APPLICATION_JSON_VALUE)
public class AcceptanceResource {

    private final AcceptanceService acceptanceService;

    public AcceptanceResource(final AcceptanceService acceptanceService) {
        this.acceptanceService = acceptanceService;
    }

    @GetMapping
    public ResponseEntity<List<AcceptanceDTO>> getAllAcceptances() {
        return ResponseEntity.ok(acceptanceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcceptanceDTO> getAcceptance(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(acceptanceService.get(id));
    }
    @GetMapping("exist/plan-detail/{id}")
    public ResponseEntity<Integer> checkAcceptanceExistPlanDetail(
            @PathVariable(name = "id") final Long id) {
        Integer exists = acceptanceService.checkIfExistByIdPlanDetail(id);
        return ResponseEntity.ok(exists);
    }
    @GetMapping("exist/error-report/{id}")
    public ResponseEntity<Integer> checkAcceptanceExistErrorReport(
            @PathVariable(name = "id") final Long id) {
        Integer exists = acceptanceService.checkIfExistByIdPlanDetail(id);
        return ResponseEntity.ok(exists);
    }
    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAcceptance(
            @RequestBody @Valid final AcceptanceDTO acceptanceDTO) {
        final Long createdId = acceptanceService.create(acceptanceDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAcceptance(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final AcceptanceDTO acceptanceDTO) {
        acceptanceService.update(id, acceptanceDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAcceptance(@PathVariable(name = "id") final Long id) {
        acceptanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
