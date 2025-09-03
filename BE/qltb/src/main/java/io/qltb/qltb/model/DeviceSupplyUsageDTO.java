package io.qltb.qltb.model;

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

    @NotNull
    private OffsetDateTime usageDate;

    @Size(max = 255)
    private String serial;

    private String note;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    @NotNull
    private Long device;

    @NotNull
    private Long supply;

}
