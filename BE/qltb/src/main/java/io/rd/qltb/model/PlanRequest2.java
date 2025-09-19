package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PlanRequest2 {
    private PlanDTO plan;
    private List<PlanDetailDTO> planDetails;
}
