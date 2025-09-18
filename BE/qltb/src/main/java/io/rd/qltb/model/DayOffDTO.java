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

    @NotNull
    @Size(max = 50)
    private String code;

    @Size(max = 200)
    private String name;

    @Size(max = 255)
    private String listBranchId;

    @Size(max = 255)
    private String listTeamId;

    @Size(max = 255)
    private String day;

    @Size(max = 255)
    private String frequency;

    private LocalDateTime fromDate;

    private LocalDateTime toDate;

    @Size(max = 255)
    private String description;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

}
