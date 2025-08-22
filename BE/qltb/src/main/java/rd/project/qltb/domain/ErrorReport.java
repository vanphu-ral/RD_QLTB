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
@Table(name = "error_report")
public class ErrorReport {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @Column(nullable = false)
    private Integer severity;

    @Column(columnDefinition = "longtext")
    private String errorDescription;

    @Column(nullable = false, length = 200)
    private String reportedBy;

    @Column(nullable = false)
    private OffsetDateTime timeReported;

    @Column(nullable = false, columnDefinition = "tinyint", length = 1)
    private Boolean isRepaired;

    @Column(columnDefinition = "longtext")
    private String repairDescription;

    @Column(length = 200)
    private String repairedBy;

    @Column
    private OffsetDateTime timeRepaired;

    @Column
    private String user;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_result_id", nullable = false)
    private PlanResult planResult;

    @OneToMany(mappedBy = "errorReport")
    private Set<Acceptance> errorReportAcceptances = new HashSet<>();

}
