package io.rd.qltb.model;

import io.rd.qltb.domain.Branch;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TeamDTO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String manager;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private Branch branch;

}
