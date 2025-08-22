package rd.project.qltb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ErrorReportDTO {

    private Long id;

    @Size(max = 255)
    private String code;

    @NotNull
    private Integer severity;

    private String errorDescription;

    @NotNull
    @Size(max = 200)
    private String reportedBy;

    @NotNull
    private OffsetDateTime timeReported;

    @NotNull
    @JsonProperty("isRepaired")
    private Boolean isRepaired;

    private String repairDescription;

    @Size(max = 200)
    private String repairedBy;

    private OffsetDateTime timeRepaired;

    @Size(max = 255)
    private String user;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String status;

    @NotNull
    private Long planResult;

}
