package rd.project.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Device {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

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
    private Integer maintenanceCycle;

    @Column
    private OffsetDateTime dateManufacture;

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

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

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
    private Set<DeviceHistory> deviceDeviceHistories = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private Set<DeviceRelocationHistory> deviceDeviceRelocationHistories = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private Set<DeviceSupplyUsage> deviceDeviceSupplyUsages = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private Set<PlanDetail> devicePlanDetails = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private Set<PerformanceManagement> devicePerformanceManagements = new HashSet<>();

    @OneToMany(mappedBy = "device")
    private Set<DepreciationManagement> deviceDepreciationManagements = new HashSet<>();

}
