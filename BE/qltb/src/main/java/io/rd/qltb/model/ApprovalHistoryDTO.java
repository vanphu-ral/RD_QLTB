package io.rd.qltb.model;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalHistoryDTO {
    private Long id;

    private String entityType;
    private Long entityId;
    private Long workflowId;
    private  String oldApprovalData;
    private  String oldRoundStatus;
}
