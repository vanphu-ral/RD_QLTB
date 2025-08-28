package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TeamDTO {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @NotNull
    private BranchDTO branch;

}
