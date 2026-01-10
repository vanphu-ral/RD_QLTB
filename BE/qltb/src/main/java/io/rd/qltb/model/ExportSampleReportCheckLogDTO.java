package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExportSampleReportCheckLogDTO {

    private Long id;

    private String username;

    private LocalDateTime dateExport;

    private String data;

    private String file;
}
