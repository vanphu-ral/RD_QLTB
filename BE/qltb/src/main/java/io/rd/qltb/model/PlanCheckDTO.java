package io.rd.qltb.model;

import io.rd.qltb.service.PlanResultService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanCheckDTO {
    private PlanResultDTO planResult;
    private List<PlanResultDetailDTO> planResultDetail;
    private List<ErrorReportDTO> errorReport;
    private  List<SupplyReplacementDTO> supplyReplacement;
}
