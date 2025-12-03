package io.rd.qltb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = true)
    private LocalDateTime date;

    @Column(nullable = true)
    private Integer dayOfWeek;

    @Column(nullable = true)
    private String type;

    @Column(nullable = true)
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


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")      // cột FK trong DB
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Team team;
}
