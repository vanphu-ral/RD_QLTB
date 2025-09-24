package io.rd.qltb.model;

import io.rd.qltb.domain.Supply;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyDetailDTO {

    private Long id;

    private String serial;

    private LocalDateTime importDate;

    @Size(max = 255)
    private String supplier;

    private Double price;

    private String unit;

    private String currency;

    private Integer quantity;

    private Integer status;

    private Supply supply;

}
