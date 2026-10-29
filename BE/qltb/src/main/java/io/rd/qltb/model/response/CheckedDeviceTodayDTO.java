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
public class CheckedDeviceTodayDTO {

    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private String qrCode;
    private String lineName;
    private String teamName;
    private String branchName;
    private Long planResultId;
    private Integer status;
    private LocalDateTime lastCheckedTime;
}

