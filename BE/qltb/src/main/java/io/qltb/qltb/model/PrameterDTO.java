package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PrameterDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @Size(max = 200)
    private String name;

    private Double value;

    private Double min;

    private Double max;

    @Size(max = 255)
    private String unit;

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

    private Long parameterGroup;

    private Long device;

}
