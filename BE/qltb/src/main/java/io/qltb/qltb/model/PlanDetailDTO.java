package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanDetailDTO {

    private Long id;

    private Long sampleReporId;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    @Size(max = 255)
    private String user;

    @Size(max = 255)
    private String status;

    @NotNull
    private Long plan;

    @NotNull
    private Long device;

    @NotNull
    private Long deviceGroup;

}
