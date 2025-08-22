package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SampleReportDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @Size(max = 200)
    private String name;

    @Size(max = 255)
    private String frequency;

    @Size(max = 255)
    private String type;

    @Size(max = 255)
    private String status;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @NotNull
    private Integer deviceGroup;

}
