package io.qltb.qltb.rest;

import io.qltb.qltb.model.SupplyDetailDTO;
import io.qltb.qltb.service.SupplyDetailService;
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
@RequestMapping(value = "/api/supplyDetails", produces = MediaType.APPLICATION_JSON_VALUE)
public class SupplyDetailResource {

    private final SupplyDetailService supplyDetailService;

    public SupplyDetailResource(final SupplyDetailService supplyDetailService) {
        this.supplyDetailService = supplyDetailService;
    }

    @GetMapping
    public ResponseEntity<List<SupplyDetailDTO>> getAllSupplyDetails() {
        return ResponseEntity.ok(supplyDetailService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyDetailDTO> getSupplyDetail(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(supplyDetailService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSupplyDetail(
            @RequestBody @Valid final SupplyDetailDTO supplyDetailDTO) {
        final Long createdId = supplyDetailService.create(supplyDetailDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }
    @PostMapping("/creates")
    public ResponseEntity<?> creates(@RequestBody List<SupplyDetailDTO> supplyDetailDTOs) {
        return supplyDetailService.creates(supplyDetailDTOs);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSupplyDetail(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SupplyDetailDTO supplyDetailDTO) {
        supplyDetailService.update(id, supplyDetailDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSupplyDetail(@PathVariable(name = "id") final Long id) {
        supplyDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
