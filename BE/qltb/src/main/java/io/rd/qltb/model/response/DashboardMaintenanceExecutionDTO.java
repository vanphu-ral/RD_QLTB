package io.rd.qltb.model.response;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardMaintenanceExecutionDTO {

    private Long planResultId;
    private Long planDetailId;
    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private String planName;
    private LocalDateTime dateTest;
    private Integer resultStatus;
    private String branchName;
    private String teamName;
    private String lineName;

}