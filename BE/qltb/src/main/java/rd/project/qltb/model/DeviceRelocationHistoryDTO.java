package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
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
    private OffsetDateTime movedAt;

    @Size(max = 255)
    private String movedBy;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @NotNull
    private Integer device;

}
