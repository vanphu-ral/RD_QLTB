package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PerformanceManagementDTO {

    private Long id;

    @Size(max = 255)
    private String code;

    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String performance;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private Long device;

}
