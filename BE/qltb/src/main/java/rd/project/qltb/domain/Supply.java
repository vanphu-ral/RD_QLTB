package rd.project.qltb.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "supply")
public class Supply {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @Column
    private Double price;

    @Column(name = "\"description\"")
    private String description;

    @Column
    private String source;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private SupplyGroup group;

    @OneToMany(mappedBy = "supply")
    private Set<DeviceSupplyUsage> supplyDeviceSupplyUsages = new HashSet<>();

    @OneToMany(mappedBy = "supply")
    private Set<SupplyReplacement> supplySupplyReplacements = new HashSet<>();

}
