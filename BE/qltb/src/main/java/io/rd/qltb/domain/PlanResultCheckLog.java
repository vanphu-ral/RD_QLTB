package io.rd.qltb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "PlanResultCheckLogs")
@Getter
@Setter
public class PlanResultCheckLog {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String inspection;

    @Column
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_result_id", nullable = false)
    private PlanResult planResult;
}
