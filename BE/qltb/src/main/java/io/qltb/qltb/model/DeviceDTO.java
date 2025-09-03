package io.qltb.qltb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceDTO {

    private Long id;

    @NotNull
    private Long bracnId;

    @NotNull
    @Size(max = 50)
    @DeviceCodeUnique
    private String code;

    @NotNull
    @Size(max = 150)
    private String name;

    private Integer numMaterialUse;

    @Size(max = 100)
    private String serialNumber;

    @Size(max = 255)
    private String source;

    private LocalDate installationDate;

    private Integer maintenanceCycle;

    private LocalDateTime dateManufacture;

    @NotNull
    @Size(max = 50)
    private String unit;

    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "75.08")
    private BigDecimal price;

    @NotNull
    private Integer status;

    @Size(max = 100)
    private String qrcode;

    @Size(max = 255)
    private String img;

    @Size(max = 255)
    private String userManager;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    @NotNull
    private Long group;

    @NotNull
    private Long line;

}
