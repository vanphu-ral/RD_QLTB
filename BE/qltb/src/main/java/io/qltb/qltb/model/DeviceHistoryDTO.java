package io.qltb.qltb.model;

import io.qltb.qltb.domain.Device;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceHistoryDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String eventType;

    private String description;

    private LocalDateTime downtimeStart;

    private LocalDateTime downtimeEnd;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updateBy;

    @NotNull
    private Device device;

}
