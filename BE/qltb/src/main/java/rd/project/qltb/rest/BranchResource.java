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
import rd.project.qltb.model.BranchDTO;
import rd.project.qltb.service.BranchService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


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
    public ResponseEntity<BranchDTO> getBranch(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(branchService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createBranch(@RequestBody @Valid final BranchDTO branchDTO) {
        final Integer createdId = branchService.create(branchDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateBranch(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final BranchDTO branchDTO) {
        branchService.update(id, branchDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteBranch(@PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = branchService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        branchService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
