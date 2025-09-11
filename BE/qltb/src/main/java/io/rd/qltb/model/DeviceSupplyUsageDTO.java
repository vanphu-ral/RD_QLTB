package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.Supply;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceSupplyUsageDTO {

    private Long id;

    @NotNull
    private OffsetDateTime usageDate;

    @Size(max = 255)
    private String serial;

    private String note;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    @NotNull
    private Device device;

    @NotNull
    private Supply supply;

}
