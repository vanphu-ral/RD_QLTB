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
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "KeyMappings")
@Getter
@Setter
public class KeyMapping {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String frequency;

    @Column
    private Integer step;

    @Column
    private String performer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_report_id", nullable = false)
    private SampleReport sampleReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criterial_id", nullable = false)
    private Criterial criterial;

}
