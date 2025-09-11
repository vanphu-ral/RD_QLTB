package io.rd.qltb.rest;

import io.rd.qltb.model.FactoryDTO;
import io.rd.qltb.service.FactoryService;
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
@RequestMapping(value = "/api/factories", produces = MediaType.APPLICATION_JSON_VALUE)
public class FactoryResource {

    private final FactoryService factoryService;

    public FactoryResource(final FactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @GetMapping
    public ResponseEntity<List<FactoryDTO>> getAllFactories() {
        return ResponseEntity.ok(factoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactoryDTO> getFactory(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(factoryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createFactory(@RequestBody @Valid final FactoryDTO factoryDTO) {
        final Long createdId = factoryService.create(factoryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateFactory(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final FactoryDTO factoryDTO) {
        factoryService.update(id, factoryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteFactory(@PathVariable(name = "id") final Long id) {
        factoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
