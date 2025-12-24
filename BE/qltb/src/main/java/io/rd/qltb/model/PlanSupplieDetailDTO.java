package io.rd.qltb.model;

import io.rd.qltb.domain.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlanSupplieDetailDTO {

    private Long id;

    private Integer quantity;

    private String symbol;

    private String techRequired;

    @Size(max = 255)
    private String manufacturer;

    private LocalDateTime deliveryTime;

    @Size(max = 250)
    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean createdBy;

    private Boolean updatedBy;

    private Integer status;

    private PlanSupplie planSupplie;

    private DeviceGroup deviceGroup;

    private Line line;

    private Supply supply;
}
