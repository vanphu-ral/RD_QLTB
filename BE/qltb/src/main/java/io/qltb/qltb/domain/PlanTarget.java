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
public class PlanTarget {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String targetDescription;

    @Column(length = 100)
    private String targetValue;

    @Column
    private String critical;

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

    @OneToMany(mappedBy = "planTargetDevice")
    private Set<PlanTargetResult> planTargetDevicePlanTargetResults = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

}
