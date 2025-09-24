package io.rd.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DayOffDTO {

    private Long id;

    private String code;

    private String name;

    private String listBranchId;

    private String listTeamId;

    private String day;

    private String frequency;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    private String description;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    private String createdBy;


    private String updatedBy;

    private Integer status;

}
