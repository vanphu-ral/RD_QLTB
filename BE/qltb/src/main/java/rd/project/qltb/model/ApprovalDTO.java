package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApprovalDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String entityType;

    @NotNull
    private Long entityId;

    @NotNull
    @Size(max = 450)
    private String approverId;

    @NotNull
    @Size(max = 50)
    private String status;

    private OffsetDateTime signedAt;

    private String note;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

}
