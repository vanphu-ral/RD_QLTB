package io.rd.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanResultDTO {

    private Long id;

    @Size(max = 255)
    private String code;

    @NotNull
    private Long planResultDetailId;

    private String note;

    private LocalDateTime dateTest;

    private String userTest;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    @Size(max = 255)
    private Integer status;

    @Size(max = 255)
    private String statusRepair;

}
