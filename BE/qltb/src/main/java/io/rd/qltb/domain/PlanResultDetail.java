package io.rd.qltb.domain;

import io.rd.qltb.enums.OperationsStaff;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "PlanResultDetails")
@Getter
@Setter
public class PlanResultDetail {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 50)
    private String criticalCode;
    @Column
    private String criticalGroup;

    @Column(length = 200)
    private String criticalName;

    @Column
    private String frequency;

    @Column
    private Integer step;

    @Column
    private String performer;

    @Column
    private String inspectionSession; //Ca kiểm tra: ca1 ca2 ngày

    @Column
    private String examinationTime; //Thời gian kiểm tra: đầu ca giữa ca cuối ca tuần

    @Column
    private String type;

    @Column
    private String result;

    @Column
    private String note;

    @Column
    private String unit;

    @Column
    private Integer min;

    @Column
    private Integer max;
    @Column
    private String file;
    @Column
    private  String committee;// Bộ phận thực hiện
    @Column
    private String comment;// Ghi chú của bộ phận thực hiện

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
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
