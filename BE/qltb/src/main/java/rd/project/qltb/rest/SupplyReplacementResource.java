package rd.project.qltb.rest;

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
import rd.project.qltb.model.SupplyReplacementDTO;
import rd.project.qltb.service.SupplyReplacementService;


@RestController
@RequestMapping(value = "/api/supplyReplacements", produces = MediaType.APPLICATION_JSON_VALUE)
public class SupplyReplacementResource {

    private final SupplyReplacementService supplyReplacementService;

    public SupplyReplacementResource(final SupplyReplacementService supplyReplacementService) {
        this.supplyReplacementService = supplyReplacementService;
    }

    @GetMapping
    public ResponseEntity<List<SupplyReplacementDTO>> getAllSupplyReplacements() {
        return ResponseEntity.ok(supplyReplacementService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyReplacementDTO> getSupplyReplacement(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(supplyReplacementService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createSupplyReplacement(
            @RequestBody @Valid final SupplyReplacementDTO supplyReplacementDTO) {
        final Long createdId = supplyReplacementService.create(supplyReplacementDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateSupplyReplacement(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final SupplyReplacementDTO supplyReplacementDTO) {
        supplyReplacementService.update(id, supplyReplacementDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSupplyReplacement(@PathVariable(name = "id") final Long id) {
        supplyReplacementService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
