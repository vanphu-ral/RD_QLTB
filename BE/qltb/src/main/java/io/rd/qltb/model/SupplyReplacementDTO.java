package io.rd.qltb.model;

import io.rd.qltb.domain.PlanResult;
import io.rd.qltb.domain.Supply;
import io.rd.qltb.domain.SupplyDetail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyReplacementDTO {

    private Long id;

    private Integer quantity;

    private String code;

    private String name;

    private String note;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String updatedBy;

    @Size(max = 255)
    private String createdBy;

    private PlanResult planResult;

    private SupplyDetail supply;

}
