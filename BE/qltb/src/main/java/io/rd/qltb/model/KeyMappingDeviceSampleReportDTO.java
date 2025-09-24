package io.rd.qltb.model;

import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.SampleReport;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class KeyMappingDeviceSampleReportDTO {

    private Long id;

    private SampleReport sampleReport;

    private DeviceGroup deviceGroup;

}
