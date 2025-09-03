package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceRelocationHistoryDTO {

    private Long id;

    private Long oldFactoryId;

    private Long newFactoryId;

    private Long oldBranchId;

    private Long newBranchId;

    private Long oldTeamId;

    private Long newTeamId;

    private Long oldLineId;

    private Long newLineId;

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
    private Long device;

}
