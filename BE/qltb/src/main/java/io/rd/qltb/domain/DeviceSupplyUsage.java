package io.rd.qltb.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "DeviceSupplyUsages")
@Getter
@Setter
public class DeviceSupplyUsage {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime usageDate;

    @Column
    private LocalDateTime updatedAt;
    @Column
    private LocalDateTime createdAt;

    @Column(length = 100)
    private String updatedBy;

    @Column(length = 100)
    private String createdBy;

    @Column
    private Integer quantityUsed;

    @Column(columnDefinition = "longtext")
    private String description;

    @Column
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_detail_id", nullable = false)
    private SupplyDetail supplyDetail;

}
