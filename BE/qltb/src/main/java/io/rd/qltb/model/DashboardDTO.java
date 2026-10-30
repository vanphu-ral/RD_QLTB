package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import lombok.Data;

import java.util.List;

@Data
public class DashboardDTO {
    private List<DeviceDTO> workingDevices;
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

}
