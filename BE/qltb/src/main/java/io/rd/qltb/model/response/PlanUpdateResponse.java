package io.rd.qltb.model.response;

import io.rd.qltb.domain.Plan;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanUpdateResponse {
    private Plan plan;
    private String status;
    private String message;
}
