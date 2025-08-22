package rd.project.qltb.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "device_relocation_history")
public class DeviceRelocationHistory {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer oldFactoryId;

    @Column
    private Integer newFactoryId;

    @Column
    private Integer oldBranchId;

    @Column
    private Integer newBranchId;

    @Column
    private Integer oldTeamId;

    @Column
    private Integer newTeamId;

    @Column
    private Integer oldLineId;

    @Column
    private Integer newLineId;

    @Column(columnDefinition = "longtext")
    private String reason;

    @Column(nullable = false)
    private OffsetDateTime movedAt;

    @Column
    private String movedBy;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

}
