package io.rd.qltb.model;

import io.rd.qltb.domain.ApprovalGroup;
import io.rd.qltb.domain.ApprovalGroupUser;
import io.rd.qltb.domain.ApprovalWorkflow;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApprovalDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String entityType;

    @NotNull
    private Long entityId;

    @NotNull
    @Size(max = 450)
    private ApprovalGroupUser userApproval;

    @NotNull
    private ApprovalWorkflow workflow;

    @NotNull
    private ApprovalGroup group;

    @NotNull
    @Size(max = 50)
    private String status;

    private LocalDateTime signedAt;

    private String note;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

}
