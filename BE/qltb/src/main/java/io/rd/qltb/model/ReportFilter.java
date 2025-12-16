package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ReportFilter { // Bao cao su dung vat tu hang thang
    private String startDate;
    private String endDate;
    private List<Long> branchIds;
    private List<Long> teamIds;
}
