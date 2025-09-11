package io.rd.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "DeviceGroups")
@Getter
@Setter
public class DeviceGroup {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

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
    private Integer status;

    @OneToMany(mappedBy = "deviceGroup")
    private Set<SampleReport> deviceGroupSampleReports = new HashSet<>();

    @OneToMany(mappedBy = "deviceGroup")
    private Set<KeyMappingDeviceSampleReport> deviceGroupKeyMappingDeviceSampleReports = new HashSet<>();

    @OneToMany(mappedBy = "group")
    private Set<Device> groupDevices = new HashSet<>();

    @OneToMany(mappedBy = "deviceGroup")
    private Set<PlanDetail> deviceGroupPlanDetails = new HashSet<>();

}
