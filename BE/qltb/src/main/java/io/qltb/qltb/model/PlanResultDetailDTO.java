package io.qltb.qltb.model;

import io.qltb.qltb.domain.PlanResult;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanResultDetailDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    @PlanResultDetailCriticalCodeUnique
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

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private PlanResult planResult;

}
