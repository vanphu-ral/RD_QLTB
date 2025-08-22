package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceGroupDTO {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

}
