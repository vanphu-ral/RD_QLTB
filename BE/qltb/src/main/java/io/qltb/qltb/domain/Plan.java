package io.qltb.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Plan {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column
    private Long factoryId;

    @Column
    private Long branchId;

    @Column(length = 50)
    private String frequency;

    @Column
    private Integer planNumber;

    @Column(columnDefinition = "longtext")
    private String note;

    @Column(nullable = false, length = 200)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String updatedBy;

    @Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_type_id", nullable = false)
    private PlanType planType;

    @OneToMany(mappedBy = "plan")
    private Set<PlanDetail> planPlanDetails = new HashSet<>();

}
