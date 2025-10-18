package io.rd.qltb.model;

import io.rd.qltb.domain.ApprovalWorkflow;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApprovalRoundDTO {
    private Long id;
    private String entityType;
    private Long entityId;
    private Integer roundNumber;
    private Long previousRoundId;
    private ApprovalWorkflow workflow;
    private Integer status;
    private LocalDateTime createdAt;
    private String createdBy;
}
