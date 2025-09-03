package io.qltb.qltb.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DepreciationManagementDTO {

    private Long id;

    @Size(max = 255)
    private String code;

    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String depr;

    @NotNull
    @Schema(type = "string", example = "18:30")
    private LocalTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private Long device;

}
