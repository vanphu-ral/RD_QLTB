package io.rd.qltb.model.response;

import io.rd.qltb.model.ErrorReportDTO;
import io.rd.qltb.model.SupplyReplacementHistoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceComprehensiveReportDTO {
    private DeviceErrorSummaryDTO summary;

    // Phần dữ liệu từ API 2 (Chi tiết lỗi)
    private List<ErrorReportDTO> errorDetails;

    // Phần dữ liệu từ API 3 (Lịch sử thay thế)
    private List<SupplyReplacementHistoryDTO> replacementHistory;
}
