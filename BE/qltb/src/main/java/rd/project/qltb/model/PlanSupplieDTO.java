package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanSupplieDTO {

    private Long id;

    @Size(max = 150)
    private String sapCode;

    @Size(max = 150)
    private String sapName;

    @Size(max = 250)
    private String description;

    private Integer quantity;

    private Double price;

    private Integer activeValue;

    @Size(max = 450)
    private String fileScan;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    private Long plan;

}
