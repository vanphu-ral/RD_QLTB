package io.rd.qltb.model;

import io.rd.qltb.service.PlanResultService;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanCheckDTO {
    private PlanResultDTO planResult; // thông tin chung kết quả kiểm tra
    private List<PlanResultDetailDTO> planResultDetail; // chi tiết kết quả kiểm tra
    private List<ErrorReportDTO> errorReport; // thông tin báo lỗi
    private  List<SupplyReplacementDTO> supplyReplacement;// thông tin thay thế vật tư
    private List<ApprovalDTO> approvals; // thông tin ký duyệt của mau bien ban
    private PlanDetailDTO planDetail; // thông tin kế hoạch kiểm tra
    private List<DeviceCurrentSupplyDTO> deviceCurrentSupplies; // thông tin vật tư hiện có của thiết bị
    private List<SupplyReplacementHistoryDTO> supplyReplacementHistories; // lịch sử thay thế vật tư
}
