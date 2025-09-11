package io.rd.qltb.rest;

import io.rd.qltb.model.SupplyDetailDTO;
import io.rd.qltb.service.SupplyDetailService;
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
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(supplyDetailService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createSupplyDetail(
            @RequestBody @Valid final SupplyDetailDTO supplyDetailDTO) {
        final Integer createdId = supplyDetailService.create(supplyDetailDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateSupplyDetail(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final SupplyDetailDTO supplyDetailDTO) {
        supplyDetailService.update(id, supplyDetailDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSupplyDetail(@PathVariable(name = "id") final Integer id) {
        supplyDetailService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
