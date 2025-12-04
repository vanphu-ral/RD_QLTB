package io.rd.qltb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name = "Plans")
@Getter
@Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Plan {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 50)
    private String frequency;

    @Column(length = 50)
    private String planNumber;

    @Column(length = 50)
    private String userPerformer;

    @Column
    private LocalDateTime fromDate;

    @Column
    private LocalDateTime toDate;

    @Column(length = 500, name = "\"description\"")
    private String description;

    @Column(nullable = false, length = 200)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String updatedBy;

    @Column
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_type_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PlanType planType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @NotFound(action = NotFoundAction.IGNORE)
    private Team team;

    @OneToMany(mappedBy = "plan")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<PlanDetail> planPlanDetails = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_workflow_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ApprovalWorkflow approvalWorkflow;

    public Plan() {
    }

    public Plan(Long id, String code, String name, String frequency, String planNumber, String userPerformer, LocalDateTime fromDate, LocalDateTime toDate, String description, String createdBy, LocalDateTime createdAt, LocalDateTime updatedAt, String updatedBy, Integer status, PlanType planType, Factory factory, Branch branch, Team team, Set<PlanDetail> planPlanDetails, ApprovalWorkflow approvalWorkflow) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.frequency = frequency;
        this.planNumber = planNumber;
        this.userPerformer = userPerformer;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.description = description;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.status = status;
        this.planType = planType;
        this.factory = factory;
        this.branch = branch;
        this.team = team;
        this.planPlanDetails = planPlanDetails;
        this.approvalWorkflow = approvalWorkflow;
    }
}
