package io.rd.qltb.rest;

import io.rd.qltb.model.ApprovalGroupDTO;
import io.rd.qltb.service.ApprovalGroupService;
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
@RequestMapping(value = "/api/approvalGroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalGroupResource {

    private final ApprovalGroupService approvalGroupService;

    public ApprovalGroupResource(final ApprovalGroupService approvalGroupService) {
        this.approvalGroupService = approvalGroupService;
    }

    @GetMapping
    public ResponseEntity<List<ApprovalGroupDTO>> getAllApprovalGroups() {
        return ResponseEntity.ok(approvalGroupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalGroupDTO> getApprovalGroup(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(approvalGroupService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createApprovalGroup(
            @RequestBody @Valid final ApprovalGroupDTO approvalGroupDTO) {
        final Long createdId = approvalGroupService.create(approvalGroupDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateApprovalGroup(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ApprovalGroupDTO approvalGroupDTO) {
        approvalGroupService.update(id, approvalGroupDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteApprovalGroup(@PathVariable(name = "id") final Long id) {
        approvalGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
