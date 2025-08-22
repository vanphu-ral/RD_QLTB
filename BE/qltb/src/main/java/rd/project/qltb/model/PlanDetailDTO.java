package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanDetailDTO {

    private Long id;

    @Size(max = 255)
    private String frequency;

    @Size(max = 255)
    private String userSign;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String user;

    @Size(max = 255)
    private String status;

    @NotNull
    private Long plan;

    @NotNull
    private Integer device;

}
