package io.rd.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Supplies")
@Getter
@Setter
public class Supply {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "\"description\"")
    private String description;

    @Column
    private String source;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private SupplyGroup group;

    @OneToMany(mappedBy = "supply")
    private Set<SupplyDetail> supplySupplyDetails = new HashSet<>();

    @OneToMany(mappedBy = "supply")
    private Set<DeviceSupplyUsage> supplyDeviceSupplyUsages = new HashSet<>();

    @OneToMany(mappedBy = "supply")
    private Set<SupplyReplacement> supplySupplyReplacements = new HashSet<>();

    @OneToMany(mappedBy = "oldSupply")
    private Set<SupplyReplacementHistory> oldSupplySupplyReplacementHistories = new HashSet<>();

    @OneToMany(mappedBy = "newSupply")
    private Set<SupplyReplacementHistory> newSupplySupplyReplacementHistories = new HashSet<>();

}
