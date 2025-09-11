package io.rd.qltb.rest;

import io.rd.qltb.model.KeyMappingDTO;
import io.rd.qltb.service.KeyMappingService;
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
@RequestMapping(value = "/api/keyMappings", produces = MediaType.APPLICATION_JSON_VALUE)
public class KeyMappingResource {

    private final KeyMappingService keyMappingService;

    public KeyMappingResource(final KeyMappingService keyMappingService) {
        this.keyMappingService = keyMappingService;
    }

    @GetMapping
    public ResponseEntity<List<KeyMappingDTO>> getAllKeyMappings() {
        return ResponseEntity.ok(keyMappingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<KeyMappingDTO> getKeyMapping(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(keyMappingService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createKeyMapping(
            @RequestBody @Valid final KeyMappingDTO keyMappingDTO) {
        final Integer createdId = keyMappingService.create(keyMappingDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateKeyMapping(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final KeyMappingDTO keyMappingDTO) {
        keyMappingService.update(id, keyMappingDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteKeyMapping(@PathVariable(name = "id") final Integer id) {
        keyMappingService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
