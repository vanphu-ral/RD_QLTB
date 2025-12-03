package io.rd.qltb.model;

import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.Team;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DayOffCalendarDTO {
    private Long id;
    private String code;
    private String name;
    private LocalDateTime date;
    private Integer dayOfWeek;
    private String type;
    private Integer isDayOff;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Integer status;
    private Branch branch;
    private Team team;
}
