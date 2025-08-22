package rd.project.qltb.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "plan_result")
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
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String status;

    @Column
    private String statusRepair;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_detail_id", nullable = false)
    private PlanDetail planDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_result_detail_id")
    private PlanResultDetail planResultDetail;

    @OneToMany(mappedBy = "planResult")
    private Set<ErrorReport> planResultErrorReports = new HashSet<>();

    @OneToMany(mappedBy = "planResult")
    private Set<Acceptance> planResultAcceptances = new HashSet<>();

    @OneToMany(mappedBy = "planResult")
    private Set<SupplyReplacement> planResultSupplyReplacements = new HashSet<>();

}
