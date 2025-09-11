package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceRelocationHistoryDTO {

    private Long id;

    private Integer oldFactoryId;

    private Integer newFactoryId;

    private Integer oldBranchId;

    private Integer newBranchId;

    private Integer oldTeamId;

    private Integer newTeamId;

    private Integer oldLineId;

    private Integer newLineId;

    private String reason;

    @NotNull
    private LocalDateTime movedAt;

    @Size(max = 255)
    private String movedBy;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    @NotNull
    private Device device;

}
