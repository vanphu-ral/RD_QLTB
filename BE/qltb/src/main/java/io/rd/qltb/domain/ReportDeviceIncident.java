package io.rd.qltb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "ReportDeviceIncidents")
public class ReportDeviceIncident {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column
    private String code;
    @Column
    private String name;
    @Column
    private String errorDescription;
    @Column
    private String reason;
    @Column
    private String docNumber;
    @Column
    private String treatment_measures;
    @Column
    private String performer;
    @Column
    private LocalDateTime timeComplete;
    @Column
    private String listUser;
    @Column
    private String division;
    @Column
    private LocalDateTime createdAt;
    @Column
    private String createdBy;
    @Column
    private LocalDateTime updatedAt;
    @Column
    private String updatedBy;
    @Column
    private Integer status;
    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private ApprovalWorkflow workflow;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_report_id", nullable = false)
    private ErrorReport errorReport;
}
