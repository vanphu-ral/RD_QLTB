package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceGroupDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    @DeviceGroupCodeUnique
    private String code;

    @NotNull
    @Size(max = 150)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

}
