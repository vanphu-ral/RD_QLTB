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

    private String entityType;

    private Long entityId;

    private ApprovalGroupUser userApproval;

    private ApprovalWorkflow workflow;

    private ApprovalGroup group;

    private Integer status;

    private LocalDateTime signedAt;

    private String note;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

}
