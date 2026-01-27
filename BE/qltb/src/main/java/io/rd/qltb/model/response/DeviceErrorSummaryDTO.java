package io.rd.qltb.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceErrorSummaryDTO {
    private String factory;      // Xưởng
    private String branch;       // Ngành
    private String team;         // Tổ
    private String deviceGroup;  // Nhóm TB
    private Integer year;        // Năm
    private Integer month;       // Tháng
    private String deviceName;   // Tên máy
    private String deviceCode;   // Mã máy
    private Long errorCount;     // Số lần lỗi
    private Double totalDowntime; // Tổng thời gian dừng sửa chữa (giờ hoặc phút)
    private Long deviceId;
}
