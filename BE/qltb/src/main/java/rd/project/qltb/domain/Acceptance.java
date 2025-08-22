package rd.project.qltb.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "acceptance")
public class Acceptance {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String code;

    @Column
    private String name;

    @Column
    private String note;

    @Column
    private String user;

    @Column
    private OffsetDateTime timeAcceptance;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_result_id")
    private PlanResult planResult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "error_report_id")
    private ErrorReport errorReport;

}
