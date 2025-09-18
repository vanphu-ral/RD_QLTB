package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceGroupDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;
    private List<Device> groupDevices;

}
