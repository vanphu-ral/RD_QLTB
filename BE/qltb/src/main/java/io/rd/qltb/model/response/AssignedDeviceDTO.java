package io.rd.qltb.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedDeviceDTO {

    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private String qrCode;
    private String branchName;
    private String teamName;
    private String lineName;
    private Long planId;
    private String planCode;
    private String planName;
    private String planTypeCode;
    private Long planDetailId;
    private Boolean todayChecked;
    private Integer todayStatus;
    private LocalDateTime lastCheckedTime;
}
