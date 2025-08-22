package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
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

    private OffsetDateTime downtimeStart;

    private OffsetDateTime downtimeEnd;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @NotNull
    private Integer device;

}
