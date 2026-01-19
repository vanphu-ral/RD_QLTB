package io.rd.qltb.model;

import io.rd.qltb.domain.PlanTarget;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanTargetResultDTO {

    private Long id;

    private String result;

    private String note;

    private LocalDateTime executionTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private PlanTarget planTargetDevice;

}
