package io.rd.qltb.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReportResponse {
    private String branchName;
    private String branchCode;
    private List<ReportDetailResponse> teamReports;
}
