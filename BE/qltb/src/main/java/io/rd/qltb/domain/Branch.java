package io.rd.qltb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "Branches")
@Getter
@Setter
public class Branch {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500, name = "\"description\"")
    private String description;

    @Column
    private String manager;

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
    @JoinColumn(name = "factory_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Factory factory;

    @OneToMany(mappedBy = "branch")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Team> branchTeams = new HashSet<>();

    @OneToMany(mappedBy = "branch")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<Device> branchDevices = new HashSet<>();

    @OneToMany(mappedBy = "branch")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Set<SampleReport> sampleReports = new HashSet<>();

}
