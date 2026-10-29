package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.model.*;
import io.rd.qltb.model.response.*;
import io.rd.qltb.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Autowired
    private BranchService branchService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private PlanResultDetailRepository planResultDetailRepository;
    @Autowired
    private PlanDetailRepository planDetailRepository;
    @Autowired
    private SupplyReplacementHistoryRepository supplyReplacementHistoryRepository;
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ErrorReportService errorReportService;
    @Autowired
    private ErrorReportRepository errorReportRepository;
    @Autowired
    private SupplyReplacementHistoryService supplyReplacementHistoryService;

    public List<ReportResponse> getSupplyReport(ReportFilter filter) {
        List<ReportResponse> reportResponses = new ArrayList<>();

        Set<Long> teamIds = new HashSet<>(
                filter.getGroupIds() != null ? filter.getGroupIds() : Collections.emptyList()
        );

        List<Long> branchIds = filter.getBranchIds() != null ? filter.getBranchIds() : Collections.emptyList();

        for (Long branchId : branchIds) {
            ReportResponse reportResponse = new ReportResponse();
            BranchDTO branchDTO = branchService.get(branchId);
            if (branchDTO != null) {
                // Sử dụng .trim() để xóa bỏ các ký tự \r\n
                reportResponse.setBranchName(branchDTO.getName() != null ? branchDTO.getName().trim() : "");
                reportResponse.setBranchCode(branchDTO.getCode() != null ? branchDTO.getCode().trim() : "");
            }

            List<TeamDTO> teams = teamService.findByBranchId(branchId);
            List<ReportDetailResponse> teamReports = new ArrayList<>();

            if (teams != null) {
                for (TeamDTO team : teams) {
                    if (teamIds.contains(team.getId())) {
                        List<ReportSupplyResponse> reportSupplyResponses =
                                supplyReplacementHistoryRepository.getSupplyReportByTeamAndDateRange(
                                        branchId, team.getId(), filter.getStartDate(), filter.getEndDate());

                        // ✅ ĐIỀU KIỆN: Chỉ xử lý nếu danh sách supplies có dữ liệu (không rỗng)
                        if (reportSupplyResponses != null && !reportSupplyResponses.isEmpty()) {
                            ReportDetailResponse reportDetailResponse = new ReportDetailResponse();
                            reportDetailResponse.setTeamName(team.getName() != null ? team.getName().trim() : "");
                            reportDetailResponse.setTeamCode(team.getCode() != null ? team.getCode().trim() : "");
                            reportDetailResponse.setSupplies(reportSupplyResponses);

                            teamReports.add(reportDetailResponse);
                        }

                        // Loại bỏ teamId đã xử lý (giữ nguyên logic cũ của bạn)
                        teamIds.remove(team.getId());
                    }
                }
            }

            // ✅ CHỈ ADD BRANCH: Nếu danh sách teamReports có ít nhất 1 team có supplies
            if (!teamReports.isEmpty()) {
                reportResponse.setTeamReports(teamReports);
                reportResponses.add(reportResponse);
            }
        }
        return reportResponses;
    }

    public Page<Report2Response> getMaintenanceReport(ReportFilter filter, Pageable pageable) {
        List<Long> finalBranchIds = (filter.getBranchIds() == null || filter.getBranchIds().isEmpty()) ? null : filter.getBranchIds();
        Page<Report2Response> page = planResultDetailRepository.getMaintenanceReportByBranchAndDateRange(finalBranchIds, filter.getStartDate(), filter.getEndDate(), pageable);
        return page;
    }

    public Page<DeviceErrorSummaryDTO> getReport(List<Long> branchIds, List<Long> teamIds, List<Long> groupIds, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        if (fromDate == null) fromDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        if (toDate == null) toDate = LocalDateTime.now();

        return deviceRepository.getErrorSummaryReport(branchIds,teamIds, groupIds, fromDate, toDate, pageable);
    }

    public Page<DeviceComprehensiveReportDTO> getComprehensiveReport(
            List<Long> branchIds, List<Long> teamIds, List<Long> groupIds,
            LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {

        // 1. Xử lý null date (Logic cũ của API 1)
        if (fromDate == null) fromDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        if (toDate == null) toDate = LocalDateTime.now();
        final LocalDateTime finalFrom = fromDate;
        final LocalDateTime finalTo = toDate;

        // 2. Lấy danh sách Summary (Logic API 1)
        Page<DeviceErrorSummaryDTO> summaryPage = deviceRepository.getErrorSummaryReport(
                branchIds, teamIds, groupIds, finalFrom, finalTo, pageable);

        // 3. Map sang DTO tổng hợp và fetch dữ liệu con
        return summaryPage.map(summary -> {
            // Lấy DeviceID từ summary row
            Long deviceId = summary.getDeviceId(); // Giả sử trong DeviceErrorSummaryDTO có field deviceId (API 1 có select d.id)

            // Lấy chi tiết lỗi (API 2 logic) - Trả về List
            List<ErrorReportDTO> errors = errorReportRepository.findDetailErrorsList(deviceId, finalFrom, finalTo)
                    .stream()
                    .map(e -> errorReportService.mapToDTO(e, new ErrorReportDTO())) // Hàm map có sẵn của bạn
                    .collect(Collectors.toList());

            // Lấy lịch sử thay thế (API 3 logic) - Trả về List
            List<SupplyReplacementHistoryDTO> history = supplyReplacementHistoryRepository.findHistoryByDeviceList(deviceId, finalFrom, finalTo)
                    .stream()
                    .map(h -> {
                        SupplyReplacementHistoryDTO dto = new SupplyReplacementHistoryDTO();
                        return supplyReplacementHistoryService.mapToDTO(h, dto); // Hàm map có sẵn của bạn
                    })
                    .collect(Collectors.toList());

            // Trả về DTO tổng hợp
            return new DeviceComprehensiveReportDTO(summary, errors, history);
        });
    }

    /**
     * Lấy Sổ theo dõi sửa chữa, bảo dưỡng thiết bị (Biểu mẫu chuẩn BM08)
     * Tổng hợp từ 2 nguồn: Sự cố sửa chữa (ErrorReport) + Bảo dưỡng định kỳ (PlanDetail MAINTENANCE)
     */
    public DeviceMaintenanceLogDTO getDeviceMaintenanceLog(Long deviceId, Integer year) {
        if (deviceId == null) {
            return new DeviceMaintenanceLogDTO();
        }

        Device device = deviceRepository.findById(deviceId).orElse(null);
        if (device == null) {
            return new DeviceMaintenanceLogDTO();
        }

        if (year == null) {
            year = LocalDateTime.now().getYear();
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String branchName = device.getBranch() != null ? device.getBranch().getName() : "";
        String teamName = device.getTeam() != null ? device.getTeam().getName() : "";
        String qrCode = device.getQrCode() != null && !device.getQrCode().isBlank() ? device.getQrCode() : device.getCode();
        String lineName = "";
        if (device.getLine() != null) {
            lineName = device.getLine().getName() != null && !device.getLine().getName().isBlank()
                    ? device.getLine().getName()
                    : device.getLine().getCode();
        }

        // Format đơn vị theo cấu trúc: LED; Xưởng LED - Điện tử & TBCS; Ngành...
        String workshopName = "Xưởng " + (!branchName.isEmpty() ? branchName : "Sản xuất");

        DeviceMaintenanceLogDTO.DeviceInfo deviceInfo = DeviceMaintenanceLogDTO.DeviceInfo.builder()
                .deviceId(device.getId())
                .deviceCode(device.getCode())
                .qrCode(qrCode)
                .deviceName(device.getName())
                .branchName(branchName)
                .workshopName(workshopName)
                .teamName(teamName)
                .lineName(lineName)
                .build();

        List<DeviceMaintenanceLogDTO.Item> items = new ArrayList<>();

        // 1. Nguồn 1: Sửa chữa sự cố (ErrorReport)
        List<ErrorReport> errorReports = errorReportRepository.findRepairsByDeviceAndYear(deviceId, year);
        if (errorReports != null) {
            for (ErrorReport er : errorReports) {
                LocalDateTime rawDate = er.getTimeRepaired() != null ? er.getTimeRepaired() : er.getTimeReported();
                if (rawDate == null) {
                    rawDate = er.getUpdatedAt() != null ? er.getUpdatedAt() : er.getCreatedAt();
                }

                // Nội dung sửa chữa (ưu tiên repairDescription, nếu rỗng thì lấy errorDescription hoặc name)
                String content = er.getRepairDescription();
                if (content == null || content.isBlank()) {
                    content = er.getErrorDescription();
                }
                if (content == null || content.isBlank()) {
                    content = er.getName();
                }

                // Downtime (giờ dừng máy)
                Double downtime = null;
                String downtimeStr = "";
                if (er.getTimeReported() != null && er.getTimeRepaired() != null) {
                    long minutes = Duration.between(er.getTimeReported(), er.getTimeRepaired()).toMinutes();
                    if (minutes > 0) {
                        double hours = minutes / 60.0;
                        downtime = hours;
                        if (hours == (long) hours) {
                            downtimeStr = ((long) hours) + " h";
                        } else {
                            downtimeStr = String.format(Locale.US, "%.3f h", hours).replaceAll("\\.?0+ h$", " h");
                            if (!downtimeStr.endsWith(" h")) {
                                downtimeStr += " h";
                            }
                        }
                    }
                }

                // Người thực hiện
                String performer = er.getRepairedBy();
                if (performer == null || performer.isBlank()) {
                    performer = er.getReportedBy();
                }
                if (performer == null || performer.isBlank()) {
                    performer = er.getUser();
                }

                // Vật tư sử dụng
                StringBuilder suppliesSb = new StringBuilder();
                if (er.getPlanResult() != null && er.getPlanResult().getPlanResultSupplyReplacements() != null) {
                    for (SupplyReplacement sr : er.getPlanResult().getPlanResultSupplyReplacements()) {
                        if (suppliesSb.length() > 0) suppliesSb.append(", ");
                        suppliesSb.append(sr.getName()).append(" (").append(sr.getQuantity()).append(")");
                    }
                }

                String dateStr = rawDate != null ? rawDate.format(dateFormatter) : "";

                DeviceMaintenanceLogDTO.Item item = DeviceMaintenanceLogDTO.Item.builder()
                        .isMaintenance(false)
                        .date(dateStr)
                        .rawDate(rawDate)
                        .deviceCode(device.getCode())
                        .deviceName(device.getName())
                        .lineName(lineName)
                        .content(content)
                        .downtime(downtime)
                        .downtimeStr(downtimeStr)
                        .performer(performer)
                        .supplies(suppliesSb.toString())
                        .note(er.getResult() != null ? er.getResult() : "")
                        .build();

                items.add(item);
            }
        }

        // 2. Nguồn 2: Bảo dưỡng định kỳ (PlanDetail - MAINTENANCE)
        List<PlanDetail> maintenanceDetails = planDetailRepository.findMaintenanceDetailsByDeviceAndYear(deviceId, year);
        if (maintenanceDetails != null) {
            for (PlanDetail pd : maintenanceDetails) {
                PlanResult pr = null;
                if (pd.getPlanResults() != null && !pd.getPlanResults().isEmpty()) {
                    pr = pd.getPlanResults().stream()
                            .max(Comparator.comparing(PlanResult::getUpdatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                            .orElse(null);
                }

                LocalDateTime rawDate = null;
                if (pr != null && pr.getDateTest() != null) {
                    rawDate = pr.getDateTest();
                } else if (pd.getUpdatedAt() != null) {
                    rawDate = pd.getUpdatedAt();
                } else if (pd.getEstimatedTime() != null) {
                    rawDate = pd.getEstimatedTime();
                }

                // Nội dung bảo dưỡng: ghép các tiêu chí kiểm tra PlanResultDetail
                StringBuilder contentSb = new StringBuilder();
                if (pr != null && pr.getPlanResultPlanResultDetails() != null && !pr.getPlanResultPlanResultDetails().isEmpty()) {
                    List<PlanResultDetail> sortedPrds = pr.getPlanResultPlanResultDetails().stream()
                            .sorted(Comparator.comparing(PlanResultDetail::getStep, Comparator.nullsLast(Comparator.naturalOrder()))
                                    .thenComparing(PlanResultDetail::getId))
                            .toList();

                    int idx = 1;
                    for (PlanResultDetail prd : sortedPrds) {
                        if (contentSb.length() > 0) contentSb.append("\n");
                        contentSb.append(idx++).append(": ");
                        if (prd.getCriticalGroup() != null && !prd.getCriticalGroup().isBlank()) {
                            contentSb.append(prd.getCriticalGroup()).append(" ");
                        }
                        if (prd.getCriticalName() != null) {
                            contentSb.append(prd.getCriticalName());
                        }
                    }
                }

                if (contentSb.length() == 0) {
                    if (pd.getNameDetail() != null && !pd.getNameDetail().isBlank()) {
                        contentSb.append(pd.getNameDetail());
                    } else if (pd.getDetail() != null && !pd.getDetail().isBlank()) {
                        contentSb.append(pd.getDetail());
                    } else {
                        contentSb.append("Bảo dưỡng định kỳ theo kế hoạch");
                    }
                }

                // Downtime bảo dưỡng: mặc định 1 h
                Double downtime = 1.0;
                String downtimeStr = "1 h";

                // Người thực hiện
                String performer = "";
                if (pr != null && pr.getUserTest() != null && !pr.getUserTest().isBlank()) {
                    performer = pr.getUserTest();
                } else if (pd.getManager() != null && !pd.getManager().isBlank()) {
                    performer = pd.getManager();
                } else if (pd.getPlan() != null && pd.getPlan().getUserPerformer() != null) {
                    performer = pd.getPlan().getUserPerformer();
                }

                // Vật tư sử dụng
                StringBuilder suppliesSb = new StringBuilder();
                if (pr != null && pr.getPlanResultSupplyReplacements() != null) {
                    for (SupplyReplacement sr : pr.getPlanResultSupplyReplacements()) {
                        if (suppliesSb.length() > 0) suppliesSb.append(", ");
                        suppliesSb.append(sr.getName()).append(" (").append(sr.getQuantity()).append(")");
                    }
                }

                String note = pd.getNote();
                if (note == null || note.isBlank()) {
                    if (pr != null && pr.getNote() != null) {
                        note = pr.getNote();
                    }
                }

                String dateStr = rawDate != null ? rawDate.format(dateFormatter) : "";

                DeviceMaintenanceLogDTO.Item item = DeviceMaintenanceLogDTO.Item.builder()
                        .isMaintenance(true)
                        .date(dateStr)
                        .rawDate(rawDate)
                        .deviceCode(device.getCode())
                        .deviceName(device.getName())
                        .lineName(lineName)
                        .content(contentSb.toString())
                        .downtime(downtime)
                        .downtimeStr(downtimeStr)
                        .performer(performer)
                        .supplies(suppliesSb.toString())
                        .note(note != null ? note : "")
                        .build();

                items.add(item);
            }
        }

        // 3. Sắp xếp danh sách theo rawDate giảm dần
        items.sort((a, b) -> {
            if (a.getRawDate() == null && b.getRawDate() == null) return 0;
            if (a.getRawDate() == null) return 1;
            if (b.getRawDate() == null) return -1;
            return b.getRawDate().compareTo(a.getRawDate());
        });

        // Đánh số thứ tự STT 1, 2, 3...
        for (int i = 0; i < items.size(); i++) {
            items.get(i).setStt(i + 1);
        }

        return DeviceMaintenanceLogDTO.builder()
                .deviceInfo(deviceInfo)
                .items(items)
                .build();
    }
}
