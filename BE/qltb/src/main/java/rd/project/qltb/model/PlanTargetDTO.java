package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanTargetDTO {

    private Long id;

    @Size(max = 500)
    private String targetDescription;

    @Size(max = 100)
    private String targetValue;

    @Size(max = 255)
    private String critical;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    private Integer branch;

}
