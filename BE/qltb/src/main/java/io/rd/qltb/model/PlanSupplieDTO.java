package io.rd.qltb.model;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.Factory;
import io.rd.qltb.domain.Team;
import io.rd.qltb.enums.PlanSupplieType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanSupplieDTO {

    private Long id;

    private String code;

    private String name;

    private PlanSupplieType type;

    private String planNumber;

    private String userPerformer;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private Factory factory;

    private Branch branch;

    private Team team;

    private ApprovalWorkflow approvalWorkflow;

    private List<PlanSupplieDetailDTO> planSupplieDetails;

}
