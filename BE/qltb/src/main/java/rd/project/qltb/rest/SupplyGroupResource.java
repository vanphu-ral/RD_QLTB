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
import rd.project.qltb.model.SupplyGroupDTO;
import rd.project.qltb.service.SupplyGroupService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/supplyGroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class SupplyGroupResource {

    private final SupplyGroupService supplyGroupService;

    public SupplyGroupResource(final SupplyGroupService supplyGroupService) {
        this.supplyGroupService = supplyGroupService;
    }

    @GetMapping
    public ResponseEntity<List<SupplyGroupDTO>> getAllSupplyGroups() {
        return ResponseEntity.ok(supplyGroupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyGroupDTO> getSupplyGroup(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(supplyGroupService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createSupplyGroup(
            @RequestBody @Valid final SupplyGroupDTO supplyGroupDTO) {
        final Integer createdId = supplyGroupService.create(supplyGroupDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateSupplyGroup(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final SupplyGroupDTO supplyGroupDTO) {
        supplyGroupService.update(id, supplyGroupDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSupplyGroup(@PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = supplyGroupService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        supplyGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
