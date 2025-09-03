package io.qltb.qltb.model;

import io.qltb.qltb.domain.Branch;
import io.qltb.qltb.domain.Factory;
import io.qltb.qltb.domain.Line;
import io.qltb.qltb.domain.Team;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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

    private LocalDateTime publishDate;

    @Size(max = 150)
    private String publishNum;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private Factory factory;

    private Branch branch;

    private Team team;

    private Line line;

}
