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
import rd.project.qltb.model.ApprovalDTO;
import rd.project.qltb.service.ApprovalService;


@RestController
@RequestMapping(value = "/api/approvals", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalResource {

    private final ApprovalService approvalService;

    public ApprovalResource(final ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public ResponseEntity<List<ApprovalDTO>> getAllApprovals() {
        return ResponseEntity.ok(approvalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalDTO> getApproval(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(approvalService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createApproval(@RequestBody @Valid final ApprovalDTO approvalDTO) {
        final Long createdId = approvalService.create(approvalDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateApproval(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ApprovalDTO approvalDTO) {
        approvalService.update(id, approvalDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteApproval(@PathVariable(name = "id") final Long id) {
        approvalService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
