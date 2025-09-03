package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanTargetResultDTO {

    private Long id;

    @Size(max = 255)
    private String result;

    @Size(max = 255)
    private String note;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private Long planTargetDevice;

}
