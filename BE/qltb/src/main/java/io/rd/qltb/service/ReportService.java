package io.rd.qltb.service;

import io.rd.qltb.model.BranchDTO;
import io.rd.qltb.model.ReportFilter;
import io.rd.qltb.model.TeamDTO;
import io.rd.qltb.model.response.*;
import io.rd.qltb.repos.DeviceRepository;
import io.rd.qltb.repos.PlanResultDetailRepository;
import io.rd.qltb.repos.SupplyReplacementHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportService {
    @Autowired
    private BranchService branchService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private PlanResultDetailRepository planResultDetailRepository;
    @Autowired
    private SupplyReplacementHistoryRepository supplyReplacementHistoryRepository;
    @Autowired
    private DeviceRepository deviceRepository;

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
}
