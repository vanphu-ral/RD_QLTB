package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanResultDetailDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String criticalCode;

    @Size(max = 200)
    private String criticalName;

    @Size(max = 255)
    private String frequency;

    @Size(max = 255)
    private String type;

    @Size(max = 255)
    private String result;

    @Size(max = 255)
    private String note;

    @Size(max = 255)
    private String unit;

    private Integer min;

    private Integer max;

    @Size(max = 255)
    private String status;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    private Long sampleReport;

}
