package rd.project.qltb.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "approval")
public class Approval {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 450)
    private String approverId;

    @Column(nullable = false, length = 50)
    private String status;

    @Column
    private OffsetDateTime signedAt;

    @Column(columnDefinition = "longtext")
    private String note;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;
}
