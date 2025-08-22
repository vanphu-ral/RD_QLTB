package rd.project.qltb.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "plan_supplie")
public class PlanSupplie {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150)
    private String sapCode;

    @Column(length = 150)
    private String sapName;

    @Column(length = 250, name = "\"description\"")
    private String description;

    @Column
    private Integer quantity;

    @Column
    private Double price;

    @Column
    private Integer activeValue;

    @Column(length = 450)
    private String fileScan;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

}
