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

    private String code;

    private String name;

    private String description;

    private String manager;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    private String createdBy;


    private String updatedBy;

    private Integer status;

    private Team team;

}
