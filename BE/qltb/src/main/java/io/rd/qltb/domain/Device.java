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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Devices")
@Getter
@Setter
public class Device {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column
    private Integer numMaterialUse;

    @Column(length = 100)
    private String serialNumber;

    @Column
    private String source;

    @Column
    private LocalDate installationDate;

    @Column
    private String maintenanceCycle;

    @Column
    private LocalDateTime dateManufacture;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer status;

    @Column(length = 100)
    private String qrcode;

    @Column
    private String img;

    @Column
    private String userManager;

    @Column(length = 500, name = "\"description\"")
    private String description;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    private String supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private DeviceGroup group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @OneToMany(mappedBy = "device")
    private Set<Prameter> devicePrameters = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private Set<DeviceRelocationHistory> deviceDeviceRelocationHistories = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private Set<DeviceSupplyUsage> deviceDeviceSupplyUsages = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private Set<PlanDetail> devicePlanDetails = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

}
