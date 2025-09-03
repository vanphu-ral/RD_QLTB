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
public class PlanResult {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @Column(columnDefinition = "longtext")
    private String note;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    private String status;

    @Column
    private String statusRepair;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_result_detail_id", nullable = false)
    private PlanDetail planResultDetail;

    @OneToMany(mappedBy = "planResult")
    private Set<SupplyReplacement> planResultSupplyReplacements = new HashSet<>();

    @OneToMany(mappedBy = "planResult")
    private Set<ErrorReport> planResultErrorReports = new HashSet<>();

    @OneToMany(mappedBy = "planResult")
    private Set<Acceptance> planResultAcceptances = new HashSet<>();

    @OneToMany(mappedBy = "planResult")
    private Set<PlanResultDetail> planResultPlanResultDetails = new HashSet<>();

}
