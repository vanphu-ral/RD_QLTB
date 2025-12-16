package io.rd.qltb.model.response;

import io.rd.qltb.model.TeamDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ReportDetailResponse {
    private String teamName;
    private String teamCode;
    private List<ReportSupplyResponse> supplies;
}
