package io.rd.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Acceptances")
@Getter
@Setter
public class Acceptance {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @Column
    private String name;

    @Column(columnDefinition = "longtext")
    private String note;
    @Column
    private String docNumber;
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    @Column
    private String implementingUnit;
    @Column
    private LocalDateTime dateRecord;
    @Column
    private String user;
    @Column
    private Integer type;
    @Column
    private String result;
    @Column
    private Integer safe;
    @Column
    private Integer quality;
    @Column
    private Integer productivity;
    @Column
    private String actionMore;
    @Column
    private String responsibility;
    @Column
    private String limitation;
    @Column
    private LocalDateTime fromDateAcceptance;
    @Column
    private LocalDateTime toDateAcceptance;
    @Column
    private LocalDateTime fromDatePerform;
    @Column
    private LocalDateTime toDatePerform;

    @Column
    private LocalDateTime timeAcceptance;

    @Column
    private Long planDetailId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_result_id")
    private PlanResult planResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_report_id")
    private ErrorReport errorReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_workflow_id")
    private ApprovalWorkflow approvalWorkflow;

}
