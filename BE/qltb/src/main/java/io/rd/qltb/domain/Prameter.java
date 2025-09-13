package io.rd.qltb.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Prameters")
@Getter
@Setter
public class Prameter {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(length = 200)
    private String name;

    @Column(name = "\"description\"")
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
    private Integer status;

    @OneToMany(mappedBy = "parameter")
    private Set<DeviceParameterUse> parameterDeviceParameterUses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_group_id")
    private PrameterGroup parameterGroup;

}

