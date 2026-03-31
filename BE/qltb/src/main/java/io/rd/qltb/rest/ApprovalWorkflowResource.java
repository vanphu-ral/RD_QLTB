package io.rd.qltb.rest;

import io.rd.qltb.model.ApprovalWorkflowDTO;
import io.rd.qltb.service.ApprovalWorkflowService;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping(value = "/api/approvalWorkflows", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalWorkflowResource {

    private final ApprovalWorkflowService approvalWorkflowService;

    public ApprovalWorkflowResource(final ApprovalWorkflowService approvalWorkflowService) {
        this.approvalWorkflowService = approvalWorkflowService;
    }

    @GetMapping
    public ResponseEntity<List<ApprovalWorkflowDTO>> getAllApprovalWorkflows() {
        return ResponseEntity.ok(approvalWorkflowService.findAll());
    }
    @GetMapping("/approve")
    public ResponseEntity<List<ApprovalWorkflowDTO>> getAllApprovalWorkflowsByApprove() {
        return ResponseEntity.ok(approvalWorkflowService.findAllByApprove());
    }
    
    @GetMapping("/branch/approve")
    public ResponseEntity<List<ApprovalWorkflowDTO>> getAllApprovalWorkflowsByBranchAndApprove(@RequestParam String branchName) {
        return ResponseEntity.ok(approvalWorkflowService.findAllByBranchAndApprove(branchName));
    }


    @GetMapping("/paged")
    public ResponseEntity<Page<ApprovalWorkflowDTO>> getApprovalWorkflows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam MultiValueMap<String, String> params) {

        // Loại bỏ param "page"
        params.remove("page");

        Map<String, Object> filters = new HashMap<>();
        params.forEach((k, v) -> filters.put(k, v.get(0)));

        Page<ApprovalWorkflowDTO> result = approvalWorkflowService.findWorkflowsPaged(filters, page);
        return ResponseEntity.ok(result);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalWorkflowDTO> getApprovalWorkflow(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(approvalWorkflowService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createApprovalWorkflow(
            @RequestBody @Valid final ApprovalWorkflowDTO approvalWorkflowDTO) {
        final Long createdId = approvalWorkflowService.create(approvalWorkflowDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateApprovalWorkflow(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ApprovalWorkflowDTO approvalWorkflowDTO) {
        approvalWorkflowService.update(id, approvalWorkflowDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteApprovalWorkflow(@PathVariable(name = "id") final Long id) {
        approvalWorkflowService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
