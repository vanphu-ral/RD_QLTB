package io.rd.qltb.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMaintenanceLogDTO {

    private DeviceInfo deviceInfo;
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceInfo {
        private Long deviceId;
        private String deviceCode;
        private String qrCode;
        private String deviceName;
        private String branchName;
        private String workshopName;
        private String teamName;
        private String lineName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Integer stt;
        private Boolean isMaintenance; // true: bảo dưỡng định kỳ (thêm [*]), false: sửa chữa sự cố
        private String date;           // dd/MM/yyyy
        private LocalDateTime rawDate; // Dùng để sắp xếp
        private String deviceCode;
        private String deviceName;
        private String lineName;
        private String content;        // Nội dung sửa chữa / bảo dưỡng
        private Double downtime;       // Thời gian dừng máy (giờ)
        private String downtimeStr;    // "23.983 h" hoặc "1 h"
        private String performer;      // Người thực hiện
        private String supplies;       // Vật tư sử dụng
        private String note;           // Ghi chú
    }
}
