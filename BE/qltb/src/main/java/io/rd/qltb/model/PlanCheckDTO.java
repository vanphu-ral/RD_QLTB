package io.rd.qltb.model;

import io.rd.qltb.service.PlanResultService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanCheckDTO {
    private PlanResultDTO planResultDTO;
    private List<PlanResultDetailDTO> planResultDetailDTOS;
    private List<ErrorReportDTO> errorReportDTOS;
    private  List<SupplyReplacementDTO> supplyReplacementDTOS;
}
