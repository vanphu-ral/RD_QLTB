package io.rd.qltb.model;

import io.rd.qltb.domain.Supply;
import io.rd.qltb.domain.SupplyDetail;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyReplacementHistoryDTO {

    private Long id;

    private Integer quantityOld;

    private Integer quantityChange;

    private String reason;

    private Long planId;

    private Long planResultId;

    private LocalDateTime createdAt;

    @Size(max = 100)
    private String createdBy;

    private SupplyDetail oldSupplyDetail;

    private SupplyDetail newSupplyDetail;

}
