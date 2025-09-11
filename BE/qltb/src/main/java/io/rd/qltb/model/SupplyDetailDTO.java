package io.rd.qltb.model;

import io.rd.qltb.domain.Supply;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyDetailDTO {

    private Integer id;

    @Size(max = 255)
    private String serial;

    private OffsetDateTime importDate;

    @Size(max = 255)
    private String supplier;

    private Integer status;

    @NotNull
    private Supply supply;

}
