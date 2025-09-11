package io.rd.qltb.model;

import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.SampleReport;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class KeyMappingDeviceSampleReportDTO {

    private Integer id;

    @NotNull
    private SampleReport sampleReport;

    @NotNull
    private DeviceGroup deviceGroup;

}
