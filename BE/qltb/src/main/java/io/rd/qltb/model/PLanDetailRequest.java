package io.rd.qltb.model;

import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.SampleReport;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PLanDetailRequest {
    private DeviceGroupDTO deviceGroup;
    private SampleReportDTO sampleReport;
}
