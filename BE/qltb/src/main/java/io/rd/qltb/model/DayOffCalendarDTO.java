package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DayOffCalendarDTO {
    private Long id;
    private Integer branchId;
    private Integer teamId;
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
}
