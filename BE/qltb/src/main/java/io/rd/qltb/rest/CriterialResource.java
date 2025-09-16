package io.rd.qltb.rest;

import io.rd.qltb.model.CriterialDTO;
import io.rd.qltb.model.SupplyDetailDTO;
import io.rd.qltb.service.CriterialService;
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
@RequestMapping(value = "/api/criterials", produces = MediaType.APPLICATION_JSON_VALUE)
public class CriterialResource {

    private final CriterialService criterialService;

    public CriterialResource(final CriterialService criterialService) {
        this.criterialService = criterialService;
    }

    @GetMapping
    public ResponseEntity<List<CriterialDTO>> getAllCriterials() {
        return ResponseEntity.ok(criterialService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterialDTO> getCriterial(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(criterialService.get(id));
    }

    @GetMapping("/ByCriterialGroup/{criterialGroupId}")
    public ResponseEntity<List<CriterialDTO>> getBySupply(@PathVariable Long criterialGroupId) {
        return ResponseEntity.ok(criterialService.getByCriterialGroup(criterialGroupId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCriterial(
            @RequestBody @Valid final CriterialDTO criterialDTO) {
        final Long createdId = criterialService.create(criterialDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateCriterial(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final CriterialDTO criterialDTO) {
        criterialService.update(id, criterialDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCriterial(@PathVariable(name = "id") final Long id) {
        criterialService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
