package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ReportFilter { // Báo cáo sử dụng vật tư filter
    private String startDate;
    private String endDate;
    private List<Long> branchIds;
    private List<Long> groupIds;
}
