package rd.project.qltb.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DeviceDTO {

    private Integer id;

    @NotNull
    @Size(max = 50)
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

    private OffsetDateTime dateManufacture;

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
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @NotNull
    private Integer group;

    @NotNull
    private Integer line;

    @NotNull
    private Integer branch;

}
