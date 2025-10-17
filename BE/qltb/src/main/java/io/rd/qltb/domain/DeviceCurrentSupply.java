package io.rd.qltb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


@Entity
@Getter
@Setter
public class DeviceCurrentSupply {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer quantity;

    @Column
    private Integer status;

    @Column
    private LocalDateTime lastReplacementDate;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 100)
    private String updatedBy;

    @Column(length = 100)
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_detail_id", nullable = false)
    private SupplyDetail supplyDetail;

}
