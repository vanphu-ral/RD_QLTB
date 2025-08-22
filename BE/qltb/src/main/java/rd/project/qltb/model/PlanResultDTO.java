package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanResultDTO {

    private Long id;

    @Size(max = 255)
    private String code;

    private String note;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String status;

    @Size(max = 255)
    private String statusRepair;

    @NotNull
    private Long planDetail;

    private Long planResultDetail;

}
