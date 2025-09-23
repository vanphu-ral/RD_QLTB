package io.rd.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "ErrorReports")
@Getter
@Setter
public class ErrorReport {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;
    @Column
    private String name;

    @Column(nullable = false)
    private Integer severity;

    @Column(columnDefinition = "longtext")
    private String errorDescription;

    @Column(nullable = false, length = 200)
    private String reportedBy;

    @Column(nullable = false)
    private LocalDateTime timeReported;

    @Column(nullable = false, columnDefinition = "tinyint", length = 1)
    private Boolean isRepaired;

    @Column(columnDefinition = "longtext")
    private String repairDescription;

    @Column(length = 200)
    private String repairedBy;

    @Column
    private LocalDateTime timeRepaired;

    @Column
    private String user;

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
    @JoinColumn(name = "plan_result_id", nullable = false)
    private PlanResult planResult;

    @OneToMany(mappedBy = "errorReport")
    private Set<Acceptance> errorReportAcceptances = new HashSet<>();

}
