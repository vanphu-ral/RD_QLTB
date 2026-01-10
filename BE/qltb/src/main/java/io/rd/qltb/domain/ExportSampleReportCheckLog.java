package io.rd.qltb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ExportSampleReportCheckLogs")
@Getter
@Setter
public class ExportSampleReportCheckLog {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String username;

    @Column
    private LocalDateTime dateExport;

    @Column
    private String data;

    @Column
    private String file;
}
