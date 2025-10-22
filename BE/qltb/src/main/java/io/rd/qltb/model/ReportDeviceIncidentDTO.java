package io.rd.qltb.model;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.ErrorReport;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class ReportDeviceIncidentDTO {
    private Long id;
    private String code;
    private String name;
    private String errorDescription;
    private String reason;
    private String treatment_measures;
    private String performer;
    private LocalDateTime timeComplete;
    private String listUser;
    private String division;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    private Integer status;
    private ApprovalWorkflow workflow;
    private ErrorReport errorReport;
}
