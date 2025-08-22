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
@Table(name = "sample_report")
public class SampleReport {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(length = 200)
    private String name;

    @Column
    private String frequency;

    @Column
    private String type;

    @Column
    private String status;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_group_id", nullable = false)
    private DeviceGroup deviceGroup;

    @OneToMany(mappedBy = "sampleReport")
    private Set<Plan> sampleReportPlans = new HashSet<>();

    @OneToMany(mappedBy = "sampleReport")
    private Set<PlanResultDetail> sampleReportPlanResultDetails = new HashSet<>();

}
