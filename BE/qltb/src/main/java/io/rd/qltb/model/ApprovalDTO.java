package io.rd.qltb.model;

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
    private String userApprovalId;

    @NotNull
    private Long workflowId;

    @NotNull
    private Long groupId;

    @NotNull
    @Size(max = 50)
    private String status;

    private LocalDateTime signedAt;

    private String note;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

}
