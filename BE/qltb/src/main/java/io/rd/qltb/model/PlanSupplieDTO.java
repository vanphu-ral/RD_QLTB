package io.rd.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanSupplieDTO {

    private Integer id;

    @Size(max = 150)
    private String code;

    @Size(max = 150)
    private String name;

    @Size(max = 250)
    private String description;

    private Integer quantity;

    private Double price;

    private Integer activeValue;

    @Size(max = 450)
    private String fileScan;

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
