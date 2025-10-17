package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.SupplyDetail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Getter
@Setter
public class DeviceCurrentSupplyDTO {

    private Long id;

    private Integer quantity;

    private Integer status;

    private LocalDateTime lastReplacementDate;

    private Device device;
    private SupplyDetail supplyDetail;

}
