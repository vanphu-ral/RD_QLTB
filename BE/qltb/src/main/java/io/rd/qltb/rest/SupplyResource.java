package io.rd.qltb.rest;

import io.rd.qltb.model.SupplyDTO;
import io.rd.qltb.service.SupplyService;
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
@RequestMapping(value = "/api/supplies", produces = MediaType.APPLICATION_JSON_VALUE)
public class SupplyResource {

    private final SupplyService supplyService;

    public SupplyResource(final SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @GetMapping
    public ResponseEntity<List<SupplyDTO>> getAllSupplies() {
        return ResponseEntity.ok(supplyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyDTO> getSupply(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(supplyService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSupply(@RequestBody @Valid final SupplyDTO supplyDTO) {
        final Long createdId = supplyService.create(supplyDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSupply(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SupplyDTO supplyDTO) {
        supplyService.update(id, supplyDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSupply(@PathVariable(name = "id") final Long id) {
        supplyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
