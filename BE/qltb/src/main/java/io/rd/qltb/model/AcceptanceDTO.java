package io.rd.qltb.model;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.ErrorReport;
import io.rd.qltb.domain.PlanResult;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AcceptanceDTO {

    private Long id;

    private String code;

    private String name;
    private Integer type;
    private String note;
    private String docNumber;
    private Device device;
    private String implementingUnit;
    private LocalDateTime dateRecord;
    private String user;
    private String result;
    private Integer safe;
    private Integer quality;
    private Integer productivity;
    private String actionMore;
    private String responsibility;
    private String limitation;
    private LocalDateTime fromDateAcceptance;
    private LocalDateTime toDateAcceptance;
    private LocalDateTime fromDatePerform;
    private LocalDateTime toDatePerform;

    private LocalDateTime timeAcceptance;

    private Long planDetailId;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    private String createdBy;


    private String updatedBy;

    private Integer status;

    private PlanResult planResult;

    private ErrorReport errorReport;

    private ApprovalWorkflow approvalWorkflow;

}
