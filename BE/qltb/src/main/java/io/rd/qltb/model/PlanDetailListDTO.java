package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlanDetailListDTO {
    private Long id;
    private String serial;
    private String manager;
    private String detail;
    private Integer status;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long deviceId;
    private String deviceCode;
    private String deviceName;

    private Long deviceGroupId;
    private String deviceGroupCode;
    private String deviceGroupName;

    private Long sampleReportId;
    private String sampleReportCode;
    private String sampleReportName;
}
