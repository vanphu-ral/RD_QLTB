package io.rd.qltb.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "Teams")
@Getter
@Setter
public class Team {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200,unique = true)
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
    @JoinColumn(name = "branch_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Branch branch;

    @OneToMany(mappedBy = "team")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Set<Line> teamLines = new HashSet<>();

    @OneToMany(mappedBy = "team")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JsonIgnore
    private Set<Device> teamDevices = new HashSet<>();

    public Team() {
    }

    public Team(Long id, String code, String name, String description, String manager, LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String updatedBy, Integer status, Branch branch, Set<Line> teamLines, Set<Device> teamDevices) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.status = status;
        this.branch = branch;
        this.teamLines = teamLines;
        this.teamDevices = teamDevices;
    }
}
