package io.rd.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "DeviceRelocationHistories")
@Getter
@Setter
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
    private LocalDateTime movedAt;

    @Column
    private String movedBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

}
