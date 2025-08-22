package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SupplyReplacementDTO {

    private Long id;

    @NotNull
    private Integer quantity;

    @Size(max = 255)
    private String sapCode;

    @Size(max = 255)
    private String sapName;

    private String note;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @NotNull
    private Long planResult;

    @NotNull
    private Integer supply;

}
