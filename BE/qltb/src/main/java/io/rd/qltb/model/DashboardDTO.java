package io.rd.qltb.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class DashboardDTO {
    /** Chỉ dùng khi cần tương thích cũ; API /data hiện chỉ trả các chỉ số số học. */
    @Deprecated
    private java.util.List<DeviceDTO> workingDevices;
    private Integer totalDevices; //
    private Integer totalWorkingDevices; //
    private Integer totalDevicesInMaintenance; // thiet bi bao tri so voi ke hoach thang
    private Integer totalDevicesMaintained; // thiet bi bao tri so voi ke hoach nam
    private Integer totalMaintained;// so thiết bị đã được bảo trì bảo dưỡng
    private Integer maintenanceExecutionCount; // Số lần thực hiện bảo trì bảo dưỡng
    private Integer acceptedPeriodicMaintenanceDeviceCount; // Số thiết bị đã được nghiệm thu SC/BĐ định kỳ
    private Integer dueMaintenanceDeviceCount; // Số thiết bị đến hạn bảo trì bảo dưỡng
    private Integer faultyDeviceCount; // Số thiết bị hỏng
    private Integer criticalIncidentReportCount; // Số báo cáo sự cố nghiêm trọng

    /** Năm đang dùng để lọc KH bảo trì / lượt thực hiện (mặc định năm hiện tại nếu request không gửi year). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer year;
    /** Tháng đang dùng cho KH bảo trì tháng (mặc định tháng hiện tại nếu request không gửi month). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer month;
    /** Danh sach id ngành dang loc (chi co khi request co loc ngành). */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> appliedBranchIds;

    /** Chi tiet thoi gian (ms) tung COUNT/query — chi co khi goi /data voi includeTiming=true. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, Long> queryTimingsMs;
    /** Tong thoi gian query DB (ms), khong gom serialize JSON. */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long totalQueryTimeMs;

    public void putTiming(final String key, final long millis) {
        if (queryTimingsMs == null) {
            queryTimingsMs = new LinkedHashMap<>();
        }
        queryTimingsMs.put(key, millis);
    }

}
