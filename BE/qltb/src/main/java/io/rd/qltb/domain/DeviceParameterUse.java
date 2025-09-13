package io.rd.qltb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Entity
@Getter
@Setter
public class DeviceParameterUse {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Double value;

    @Column
    private Double min;

    @Column
    private Double max;

    @Column
    private String unit;

    @Column(name = "\"description\"")
    private String description;

    @Column
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id")
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parameter_id")
    private Prameter parameter;

}
