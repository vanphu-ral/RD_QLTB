package rd.project.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FormDTO {

    private Long id;

    @Size(max = 150)
    private String code;

    @Size(max = 150)
    private String name;

    @Size(max = 250)
    private String description;

    @Size(max = 150)
    private String fileName;

    @Size(max = 150)
    private String filePath;

    private OffsetDateTime timeCreated;

    private OffsetDateTime timeModified;

    private OffsetDateTime publishDate;

    @Size(max = 150)
    private String publishNumber;

    @Size(max = 450)
    private String appovedUser;

    @NotNull
    private OffsetDateTime createdAt;

    @NotNull
    private OffsetDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    private Integer factory;

    private Integer branch;

    private Integer team;

    private Integer line;

}
