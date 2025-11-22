package io.rd.qltb.rest;

import io.rd.qltb.domain.Approval;
import io.rd.qltb.model.ApprovalDTO;
import io.rd.qltb.model.ApprovalRequestDTO;
import io.rd.qltb.model.ApprovalResponseDTO;
import io.rd.qltb.service.ApprovalService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/approval")
    public ResponseEntity createScriptApproval(
            @AuthenticationPrincipal OidcUser oidcUser,
            @RequestBody ApprovalRequestDTO approvalRequestDTO,
            @RequestParam("entityType") String entityType) {
        approvalService.createScriptApproval(approvalRequestDTO, entityType,oidcUser.getName());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/by-user")
    public ResponseEntity<List<ApprovalResponseDTO>> getApprovalsByUser(@AuthenticationPrincipal OidcUser oidcUser) {
        List<ApprovalResponseDTO> responseList = approvalService.getAllFromTableByUserName(oidcUser.getName());
        return ResponseEntity.ok(responseList);
    }
    @GetMapping("/entity")
    public ResponseEntity<List<Approval>> findApprovalsByEntityIdAndEntityType(
            @RequestParam("entity") String entity,
            @RequestParam("entityType") String entityType
    ) {
        return ResponseEntity.ok(approvalService.findApprovalsByEntityIdAndEntityType(entity,entityType));
    }
    @GetMapping("/all")
    public ResponseEntity<List<ApprovalResponseDTO>> getApprovalsByUser() {
        List<ApprovalResponseDTO> responseList = approvalService.getAllFromTable();
        return ResponseEntity.ok(responseList);
    }

}
