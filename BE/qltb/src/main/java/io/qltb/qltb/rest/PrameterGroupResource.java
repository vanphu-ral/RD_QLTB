package io.qltb.qltb.rest;

import io.qltb.qltb.model.PrameterGroupDTO;
import io.qltb.qltb.service.PrameterGroupService;
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
@RequestMapping(value = "/api/prameterGroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrameterGroupResource {

    private final PrameterGroupService prameterGroupService;

    public PrameterGroupResource(final PrameterGroupService prameterGroupService) {
        this.prameterGroupService = prameterGroupService;
    }

    @GetMapping
    public ResponseEntity<List<PrameterGroupDTO>> getAllPrameterGroups() {
        return ResponseEntity.ok(prameterGroupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrameterGroupDTO> getPrameterGroup(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(prameterGroupService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPrameterGroup(
            @RequestBody @Valid final PrameterGroupDTO prameterGroupDTO) {
        final Long createdId = prameterGroupService.create(prameterGroupDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePrameterGroup(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PrameterGroupDTO prameterGroupDTO) {
        prameterGroupService.update(id, prameterGroupDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePrameterGroup(@PathVariable(name = "id") final Long id) {
        prameterGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
