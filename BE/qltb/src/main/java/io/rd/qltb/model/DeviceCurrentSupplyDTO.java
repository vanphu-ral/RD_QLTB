package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.SupplyDetail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class DeviceCurrentSupplyDTO {

    private Long id;

    private Integer quantity;

    private Integer status;

    private OffsetDateTime lastReplacementDate;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 100)
    private String updatedBy;

    @NotNull
    private Device device;

    @NotNull
    private SupplyDetail supplyDetail;

}
