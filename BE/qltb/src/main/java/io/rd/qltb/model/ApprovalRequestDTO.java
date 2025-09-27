package io.rd.qltb.model;

import io.rd.qltb.domain.Plan;
import io.rd.qltb.domain.SampleReport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalRequestDTO {
    private Long entityId;
    private Long WorkflowId;
}
