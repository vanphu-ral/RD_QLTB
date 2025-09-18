package io.rd.qltb.model;

import io.rd.qltb.domain.Team;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LineDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String description;

    @Size(max = 255)
    private String manager;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    @NotNull
    private Team team;

}
