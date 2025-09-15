package io.rd.qltb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.GroupApprovalName;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApprovalGroupDTO {

    private Long id;


    private Integer level;

    @JsonProperty("isRequired")
    private Boolean isRequired;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private ApprovalWorkflow workflow;

    private GroupApprovalName groupApprovalName;

}
