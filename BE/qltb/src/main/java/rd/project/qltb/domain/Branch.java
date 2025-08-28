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
@Table(name = "branch")
public class Branch {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500, name = "\"description\"")
    private String description;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Column
    private String createdBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "factory_id", nullable = false)
    private Factory factory;

    @OneToMany(mappedBy = "branch")
    private Set<Team> branchTeams = new HashSet<>();

    @OneToMany(mappedBy = "branch")
    private Set<Device> branchDevices = new HashSet<>();

    @OneToMany(mappedBy = "branch")
    private Set<DayOff> branchDayOffs = new HashSet<>();

    @OneToMany(mappedBy = "branch")
    private Set<PlanTarget> branchPlanTargets = new HashSet<>();

    @OneToMany(mappedBy = "branch")
    private Set<Form> branchForms = new HashSet<>();

}
