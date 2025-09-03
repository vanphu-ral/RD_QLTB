package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanTargetDTO {

    private Long id;

    @Size(max = 500)
    private String targetDescription;

    @Size(max = 100)
    private String targetValue;

    @Size(max = 255)
    private String critical;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private Long branch;

}
