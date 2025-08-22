package rd.project.qltb.model;

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
    private Integer quantityUsed;

    @NotNull
    private OffsetDateTime usageDate;

    private String note;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @NotNull
    private Integer device;

    @NotNull
    private Integer supply;

}
