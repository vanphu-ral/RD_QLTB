package io.qltb.qltb.model;

import io.qltb.qltb.domain.DeviceGroup;
import io.qltb.qltb.domain.SampleReport;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class KeyMappingDeviceSampleReportDTO {

    private Long id;
    private SampleReport sampleReport;
    private DeviceGroup deviceGroup;

}
