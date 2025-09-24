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

    private Long oldFactoryId;

    private Long newFactoryId;

    private Long oldBranchId;

    private Long newBranchId;

    private Long oldTeamId;

    private Long newTeamId;

    private Long oldLineId;

    private Long newLineId;

    private String reason;

    private LocalDateTime movedAt;

    private String movedBy;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Device device;

}
