package rd.project.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class PlanResultDetail {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String criticalCode;

    @Column(length = 200)
    private String criticalName;

    @Column
    private String frequency;

    @Column
    private String type;

    @Column
    private String result;

    @Column
    private String note;

    @Column
    private String unit;

    @Column
    private Integer min;

    @Column
    private Integer max;

    @Column
    private String status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_report_id")
    private SampleReport sampleReport;

    @OneToMany(mappedBy = "planResultDetail")
    private Set<PlanResult> planResultDetailPlanResults = new HashSet<>();

}
