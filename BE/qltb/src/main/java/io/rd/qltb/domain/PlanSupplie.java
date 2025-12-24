package io.rd.qltb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.rd.qltb.enums.PlanSupplieType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "PlanSupplies")
@Getter
@Setter
public class PlanSupplie {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150)
    private String code;

    @Column(length = 150)
    private String name;

    @Enumerated(EnumType.STRING) // Lưu vào DB dưới dạng chữ (ANNUAL/REPAIR_AND_MAINTENANCE)
    @Column(name = "plan_type")   // Có thể đổi tên cột nếu muốn
    private PlanSupplieType type;

    private String planNumber;

    private String userPerformer;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    private String description;

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
    @JoinColumn(name = "factory_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_workflow_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ApprovalWorkflow approvalWorkflow;

    @OneToMany(mappedBy = "planSupplie", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JsonIgnoreProperties({"planSupplie"})
    private Set<PlanSupplieDetail> planSupplieDetails = new HashSet<>();
}
