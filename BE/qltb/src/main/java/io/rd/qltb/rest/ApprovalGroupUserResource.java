package io.rd.qltb.rest;

import io.rd.qltb.model.ApprovalGroupUserDTO;
import io.rd.qltb.service.ApprovalGroupUserService;
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
@RequestMapping(value = "/api/approvalGroupUsers", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApprovalGroupUserResource {

    private final ApprovalGroupUserService approvalGroupUserService;

    public ApprovalGroupUserResource(final ApprovalGroupUserService approvalGroupUserService) {
        this.approvalGroupUserService = approvalGroupUserService;
    }

    @GetMapping
    public ResponseEntity<List<ApprovalGroupUserDTO>> getAllApprovalGroupUsers() {
        return ResponseEntity.ok(approvalGroupUserService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApprovalGroupUserDTO> getApprovalGroupUser(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(approvalGroupUserService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createApprovalGroupUser(
            @RequestBody @Valid final ApprovalGroupUserDTO approvalGroupUserDTO) {
        final Long createdId = approvalGroupUserService.create(approvalGroupUserDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateApprovalGroupUser(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ApprovalGroupUserDTO approvalGroupUserDTO) {
        approvalGroupUserService.update(id, approvalGroupUserDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteApprovalGroupUser(@PathVariable(name = "id") final Long id) {
        approvalGroupUserService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
