package io.rd.qltb.model;

import io.rd.qltb.domain.PlanDetail;
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

    private String note;

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
    private PlanDetail planDetail;

}
