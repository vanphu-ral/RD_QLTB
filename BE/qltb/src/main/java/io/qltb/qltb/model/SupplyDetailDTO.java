package io.qltb.qltb.model;

import io.qltb.qltb.domain.Supply;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyDetailDTO {

    private Long id;

    @Size(max = 255)
    private String serial;

    private LocalDateTime importDate;

    @Size(max = 255)
    private String supplier;

    private Integer status;

    @NotNull
    private Supply supply;

}
