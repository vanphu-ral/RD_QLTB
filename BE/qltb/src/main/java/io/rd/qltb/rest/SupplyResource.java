package io.rd.qltb.rest;

import io.rd.qltb.model.PlanDTO;
import io.rd.qltb.model.SupplyDTO;
import io.rd.qltb.service.SupplyService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    @GetMapping("/paged")
    public ResponseEntity<Page<SupplyDTO>> getSupplies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam Map<String, String> params) {

        // Loại bỏ param "page" khỏi filters
        params.remove("page");

        // Chuyển sang Map<String,Object> để truyền xuống service
        Map<String, Object> filters = new HashMap<>(params);

        Page<SupplyDTO> result = supplyService.findSuppliesPaged(filters, page);
        return ResponseEntity.ok(result);
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
