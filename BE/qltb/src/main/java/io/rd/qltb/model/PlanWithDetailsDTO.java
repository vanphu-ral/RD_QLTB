package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PlanWithDetailsDTO {
    private Long id;
    private String code;
    private String name;
    private String frequency;
    private String planNumber;
    private String userPerformer;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Integer status;

    private Long planTypeId;
    private String planTypeName;

    private Long factoryId;
    private String factoryName;

    private Long branchId;
    private String branchName;

    private Long approvalWorkflowId;
    private String approvalWorkflowName;

    private List<PlanDetailListDTO> details;
}
