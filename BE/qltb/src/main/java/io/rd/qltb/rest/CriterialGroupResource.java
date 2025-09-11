package io.rd.qltb.rest;

import io.rd.qltb.model.CriterialGroupDTO;
import io.rd.qltb.service.CriterialGroupService;
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
@RequestMapping(value = "/api/criterialGroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class CriterialGroupResource {

    private final CriterialGroupService criterialGroupService;

    public CriterialGroupResource(final CriterialGroupService criterialGroupService) {
        this.criterialGroupService = criterialGroupService;
    }

    @GetMapping
    public ResponseEntity<List<CriterialGroupDTO>> getAllCriterialGroups() {
        return ResponseEntity.ok(criterialGroupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CriterialGroupDTO> getCriterialGroup(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(criterialGroupService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createCriterialGroup(
            @RequestBody @Valid final CriterialGroupDTO criterialGroupDTO) {
        final Long createdId = criterialGroupService.create(criterialGroupDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateCriterialGroup(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final CriterialGroupDTO criterialGroupDTO) {
        criterialGroupService.update(id, criterialGroupDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteCriterialGroup(@PathVariable(name = "id") final Long id) {
        criterialGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
