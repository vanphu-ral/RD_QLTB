package io.rd.qltb.model.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Dòng chi tiết dashboard gắn với {@code plan_details} (kế hoạch bảo trì MAINTENANCE).
 */
@Getter
@Setter
public class DashboardPlanDetailRowDTO {

    private Long planDetailId;
    private Long planId;
    private String planName;
    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private Integer planDetailStatus;
    private LocalDateTime estimatedTime;
    private String branchName;
    private String teamName;
    private String lineName;

}