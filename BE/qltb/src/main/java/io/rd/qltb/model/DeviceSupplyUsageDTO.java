package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.Supply;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceSupplyUsageDTO {

    private Long id;

    private LocalDateTime usageDate;

    private String serial;

    private Integer quantityUsed;

    private String description;

    private Integer status;

    private Device device;

    private Supply supply;

}
