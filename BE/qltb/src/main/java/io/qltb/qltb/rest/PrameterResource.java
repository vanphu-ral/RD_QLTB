package io.qltb.qltb.rest;

import io.qltb.qltb.model.PrameterDTO;
import io.qltb.qltb.service.PrameterService;
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
@RequestMapping(value = "/api/prameters", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrameterResource {

    private final PrameterService prameterService;

    public PrameterResource(final PrameterService prameterService) {
        this.prameterService = prameterService;
    }

    @GetMapping
    public ResponseEntity<List<PrameterDTO>> getAllPrameters() {
        return ResponseEntity.ok(prameterService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrameterDTO> getPrameter(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(prameterService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createPrameter(@RequestBody @Valid final PrameterDTO prameterDTO) {
        final Long createdId = prameterService.create(prameterDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updatePrameter(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final PrameterDTO prameterDTO) {
        prameterService.update(id, prameterDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deletePrameter(@PathVariable(name = "id") final Long id) {
        prameterService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
