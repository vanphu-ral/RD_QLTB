package io.rd.qltb.model;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.Factory;
import io.rd.qltb.domain.PlanType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanDTO {

    private Long id;

    private String code;

    private String name;

    private String frequency;

    private String planNumber;

    private String userPerformer;

    private String description;

    @Size(max = 200)
    private String createdBy;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    @NotNull
    private PlanType planType;

    private Factory factory;

    private Branch branch;

    private ApprovalWorkflow approvalWorkflow;

}
