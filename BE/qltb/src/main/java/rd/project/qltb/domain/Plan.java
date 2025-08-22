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
@Table(name = "plan")
public class Plan {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column
    private Integer factoryId;

    @Column
    private Integer branchId;

    @Column(length = 50)
    private String frequency;

    @Column
    private Integer planNumber;

    @Column
    private String note;

    @Column(nullable = false, length = 200)
    private String createdBy;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy1;

    @Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_type_id", nullable = false)
    private PlanType planType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_group_id", nullable = false)
    private DeviceGroup deviceGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_report_id", nullable = false)
    private SampleReport sampleReport;

    @OneToMany(mappedBy = "plan")
    private Set<PlanDetail> planPlanDetails = new HashSet<>();

    @OneToMany(mappedBy = "plan")
    private Set<PlanSupplie> planPlanSupplies = new HashSet<>();

}
