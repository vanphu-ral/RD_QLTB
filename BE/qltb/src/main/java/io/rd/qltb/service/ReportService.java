package io.rd.qltb.service;

import io.rd.qltb.model.BranchDTO;
import io.rd.qltb.model.ReportFilter;
import io.rd.qltb.model.TeamDTO;
import io.rd.qltb.model.response.ReportDetailResponse;
import io.rd.qltb.model.response.ReportResponse;
import io.rd.qltb.model.response.ReportSupplyResponse;
import io.rd.qltb.repos.SupplyReplacementHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ReportService {
    @Autowired
    private BranchService branchService;
    @Autowired
    private TeamService teamService;

    @Autowired
    private SupplyReplacementHistoryRepository supplyReplacementHistoryRepository;

    public List<ReportResponse> getSupplyReport(ReportFilter filter) {
        List<ReportResponse> reportResponses = new ArrayList<>();
        // Dùng Set để loại bỏ nhanh
        Set<Long> teamIds = new HashSet<>(filter.getTeamIds());

        for (Long branchId : filter.getBranchIds()) {
            ReportResponse reportResponse = new ReportResponse();
            BranchDTO branchDTO = branchService.get(branchId);
            reportResponse.setBranchName(branchDTO.getName());
            reportResponse.setBranchCode(branchDTO.getCode());

            List<TeamDTO> teams = teamService.findByBranchId(branchId);
            List<ReportDetailResponse> teamReports = new ArrayList<>();

            for (TeamDTO team : teams) {
                if (teamIds.contains(team.getId())) {
                    // Lấy báo cáo chi tiết cho tổ
                    List<ReportSupplyResponse> reportSupplyResponses =
                            supplyReplacementHistoryRepository.getSupplyReportByTeamAndDateRange(
                                    branchId, team.getId(), filter.getStartDate(), filter.getEndDate());

                    ReportDetailResponse reportDetailResponse = new ReportDetailResponse();
                    reportDetailResponse.setTeamName(team.getName());
                    reportDetailResponse.setTeamCode(team.getCode());
                    reportDetailResponse.setSupplies(reportSupplyResponses);
                    teamReports.add(reportDetailResponse);

                    // ✅ Loại bỏ teamId đã xử lý
                    teamIds.remove(team.getId());

                    // ✅ Thoát vòng lặp teamIds (vì đã tìm thấy)
                    // Ở đây không cần vòng lặp teamIds nữa, chỉ cần continue sang team tiếp theo
                    continue;
                }
            }

            reportResponse.setTeamReports(teamReports);
            reportResponses.add(reportResponse);
        }
        return reportResponses;
    }

}
