package io.rd.qltb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "DayOffCalendar")
@Getter
@Setter
public class DayOffCalendar {
    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer branchId;

    @Column
    private Integer teamId;

    @Column
    private LocalDateTime date;

    @Column
    private Integer dayOfWeek;

    @Column
    private String type;

    @Column
    private Integer isDayOff;

    @Column(length = 500, name = "\"description\"")
    private String description;

    @Column(nullable = false, length = 200)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String updatedBy;

    @Column
    private Integer status;
}
