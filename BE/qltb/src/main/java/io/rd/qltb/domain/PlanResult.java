package io.rd.qltb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "PlanResults")
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

    @Column
    private LocalDateTime dateTest;

    @Column
    private String userTest;

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

    @Column
    private String statusRepair;

    @OneToMany(mappedBy = "planResult")
    private Set<PlanResultDetail> planResultPlanResultDetails = new HashSet<>();

    @OneToMany(mappedBy = "planResult")
    private Set<ErrorReport> planResultErrorReports = new HashSet<>();

    @OneToMany(mappedBy = "planResult")
    private Set<Acceptance> planResultAcceptances = new HashSet<>();

    @OneToMany(mappedBy = "planResult")
    private Set<SupplyReplacement> planResultSupplyReplacements = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_detail_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private PlanDetail planDetail;
}
