package io.rd.qltb.model;

import io.rd.qltb.domain.SupplyGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyDTO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String source;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private SupplyGroup group;

}
