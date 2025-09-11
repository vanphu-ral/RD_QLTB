package io.rd.qltb.rest;

import io.rd.qltb.model.BranchDTO;
import io.rd.qltb.service.BranchService;
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
@RequestMapping(value = "/api/branches", produces = MediaType.APPLICATION_JSON_VALUE)
public class BranchResource {

    private final BranchService branchService;

    public BranchResource(final BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public ResponseEntity<List<BranchDTO>> getAllBranches() {
        return ResponseEntity.ok(branchService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BranchDTO> getBranch(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(branchService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createBranch(@RequestBody @Valid final BranchDTO branchDTO) {
        final Long createdId = branchService.create(branchDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateBranch(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final BranchDTO branchDTO) {
        branchService.update(id, branchDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBranch(@PathVariable(name = "id") final Long id) {
        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
