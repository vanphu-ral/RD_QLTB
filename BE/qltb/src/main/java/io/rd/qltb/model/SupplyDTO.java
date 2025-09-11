package io.rd.qltb.model;

import io.rd.qltb.domain.SupplyGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyDTO {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 150)
    private String name;

    @NotNull
    private Integer quantity;

    private Double price;

    @Size(max = 255)
    private String description;

    @Size(max = 255)
    private String source;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    @NotNull
    private SupplyGroup group;

}
