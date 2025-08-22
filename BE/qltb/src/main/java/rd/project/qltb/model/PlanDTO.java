package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanDTO {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String name;

    private Integer factoryId;

    private Integer branchId;

    @Size(max = 50)
    private String frequency;

    private Integer planNumber;

    @Size(max = 255)
    private String note;

    @NotNull
    @Size(max = 200)
    private String createdBy;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy1;

    @Size(max = 255)
    private String status;

    @NotNull
    private Integer planType;

    @NotNull
    private Integer deviceGroup;

    @NotNull
    private Long sampleReport;

}
