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

    private String note;

    private LocalDateTime dateTest;

    private String userTest;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private String statusRepair;
    private PlanDetail planDetail;

}
