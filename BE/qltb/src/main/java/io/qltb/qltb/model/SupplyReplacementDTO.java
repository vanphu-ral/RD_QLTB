package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyReplacementDTO {

    private Long id;

    @NotNull
    private Integer quantity;

    @Size(max = 255)
    private String code;

    @Size(max = 255)
    private String name;

    private String note;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String updatedBy;

    @Size(max = 255)
    private String createdBy;

    @NotNull
    private Long planResult;

    @NotNull
    private Long supply;

}
