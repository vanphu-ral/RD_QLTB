package io.qltb.qltb.model;

import io.qltb.qltb.domain.PlanType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanDTO {

    private Long id;

    @NotNull
    @Size(max = 200)
    private String name;

    private Long factoryId;

    private Long branchId;

    @Size(max = 50)
    private String frequency;

    private Integer planNumber;

    private String note;

    @NotNull
    @Size(max = 200)
    private String createdBy;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String updatedBy;

    @Size(max = 255)
    private String status;

    @NotNull
    private PlanType planType;

}
