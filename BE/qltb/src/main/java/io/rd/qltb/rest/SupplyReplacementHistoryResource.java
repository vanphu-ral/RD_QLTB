package io.rd.qltb.rest;

import io.rd.qltb.model.SupplyReplacementHistoryDTO;
import io.rd.qltb.service.SupplyReplacementHistoryService;
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
@RequestMapping(value = "/api/supplyReplacementHistories", produces = MediaType.APPLICATION_JSON_VALUE)
public class SupplyReplacementHistoryResource {

    private final SupplyReplacementHistoryService supplyReplacementHistoryService;

    public SupplyReplacementHistoryResource(
            final SupplyReplacementHistoryService supplyReplacementHistoryService) {
        this.supplyReplacementHistoryService = supplyReplacementHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<SupplyReplacementHistoryDTO>> getAllSupplyReplacementHistories() {
        return ResponseEntity.ok(supplyReplacementHistoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyReplacementHistoryDTO> getSupplyReplacementHistory(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(supplyReplacementHistoryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSupplyReplacementHistory(
            @RequestBody @Valid final SupplyReplacementHistoryDTO supplyReplacementHistoryDTO) {
        final Long createdId = supplyReplacementHistoryService.create(supplyReplacementHistoryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSupplyReplacementHistory(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SupplyReplacementHistoryDTO supplyReplacementHistoryDTO) {
        supplyReplacementHistoryService.update(id, supplyReplacementHistoryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSupplyReplacementHistory(
            @PathVariable(name = "id") final Long id) {
        supplyReplacementHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
