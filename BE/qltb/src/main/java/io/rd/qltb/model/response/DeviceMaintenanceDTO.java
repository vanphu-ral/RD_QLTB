package io.rd.qltb.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DeviceMaintenanceDTO {
    private Long deviceId;
    private String deviceName;
    private Integer maintenanceTime; // Số tháng định kỳ
    private LocalDate dateTest;      // Ngày kiểm tra gần nhất
    private String estimatedTime;    // Thời gian dự kiến (từ plan_details)
    private LocalDate nextTest;      // Ngày bảo trì tiếp theo (tính toán)
    private String planName;
    private Long planResultId;
    private Long daysDiff;           // Số ngày chênh lệch (dương là trễ, âm là chưa tới)
    private String status;           // "OVERDUE", "UPCOMING", "NO_DATA"
    private String deviceGroupName;
    private String branchName;
    private String teamName;
    private String lineName;
}
