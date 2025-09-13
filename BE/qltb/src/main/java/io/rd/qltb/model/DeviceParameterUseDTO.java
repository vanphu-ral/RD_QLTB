package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.Prameter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class DeviceParameterUseDTO {

    private Long id;

    private Double value;

    private Double min;

    private Double max;

    private String unit;

    @Size(max = 255)
    private String description;

    private Integer status;

    private Device device;

    private Prameter parameter;

}
