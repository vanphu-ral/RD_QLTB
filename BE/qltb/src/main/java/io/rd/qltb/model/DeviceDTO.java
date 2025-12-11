package io.rd.qltb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.Line;
import io.rd.qltb.domain.Team;
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

    private String code;

    private String name;

    private Integer numMaterialUse;

    private String serialNumber;

    private String source;

    private LocalDate installationDate;

    private String maintenanceCycle;

    private LocalDateTime dateManufacture;

    private Integer maintenanceTime;

    private Integer depreciationPeriod;

    private Integer depreciationPercentage;

    private String unit;

    @Digits(integer = 12, fraction = 2)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(type = "string", example = "75.08")
    private BigDecimal price;

    private Integer status;

    private String qrCode;

    private String qrCodeImg;

    private Boolean isMappingScada;

    private Integer isImportant;

    private LocalDateTime timeRecieve;

    private String img;

    private String userManager;

    private String description;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private String supplier;

    private DeviceGroup group;

    private Line line;

    private Branch branch;

    private Team team;
    private Integer isHadDataPlanReport;
}
