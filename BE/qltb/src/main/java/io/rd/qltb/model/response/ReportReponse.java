package io.rd.qltb.model.response;

import lombok.Data;

@Data
public class ReportReponse {
    /** Tổng số thiết bị đang ở trạng thái hoạt động (Active) */
    private int activeAssets;

    /** Tổng tất cả các thiết bị có trong hệ thống */
    private int totalAssets;

    /** Số thiết bị có kế hoạch bảo trì trong tháng hiện tại */
    private int monthlyScheduledAssets;

    /** Tổng số thiết bị nằm trong kế hoạch năm */
    private int annualPlannedAssets;

    /** Tổng số thiết bị đã hoàn thành bảo dưỡng trong kế hoạch năm */
    private int annualCompletedAssets;

    /** Tổng số lần thực hiện các công việc bảo trì, bảo dưỡng (lượt thực hiện) */
    private long maintenanceExecutionCount;

    /** Số thiết bị đã hoàn tất nghiệm thu sau sửa chữa hoặc bảo dưỡng định kỳ */
    private int acceptedMaintenanceAssets;

    /** Số thiết bị đã đến hạn hoặc quá hạn bảo trì */
    private int dueOrOverdueAssets;

    /** Số lượng thiết bị đang báo lỗi ở thời điểm hiện tại */
    private int currentFaultyAssets;

    /** Số lượng biên bản ghi nhận các sự cố mức độ nghiêm trọng */
    private int criticalIncidentReports;
}

