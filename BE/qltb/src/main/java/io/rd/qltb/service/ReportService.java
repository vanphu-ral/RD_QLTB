package io.rd.qltb.service;

import io.rd.qltb.model.BranchDTO;
import io.rd.qltb.model.ReportFilter;
import io.rd.qltb.model.TeamDTO;
import io.rd.qltb.model.response.Report2Response;
import io.rd.qltb.model.response.ReportDetailResponse;
import io.rd.qltb.model.response.ReportResponse;
import io.rd.qltb.model.response.ReportSupplyResponse;
import io.rd.qltb.repos.PlanResultDetailRepository;
import io.rd.qltb.repos.SupplyReplacementHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public List<ReportResponse> getSupplyReport(ReportFilter filter) {
        List<ReportResponse> reportResponses = new ArrayList<>();

        // Dùng Set để loại bỏ nhanh, tránh NullPointerException
        Set<Long> teamIds = new HashSet<>(
                filter.getGroupIds() != null ? filter.getGroupIds() : Collections.emptyList()
        );

        // Nếu branchIds null thì thay bằng empty list
        List<Long> branchIds = filter.getBranchIds() != null ? filter.getBranchIds() : Collections.emptyList();

        for (Long branchId : branchIds) {
            ReportResponse reportResponse = new ReportResponse();
            BranchDTO branchDTO = branchService.get(branchId);
            if (branchDTO != null) {
                reportResponse.setBranchName(branchDTO.getName());
                reportResponse.setBranchCode(branchDTO.getCode());
            }

            List<TeamDTO> teams = teamService.findByBranchId(branchId);
            List<ReportDetailResponse> teamReports = new ArrayList<>();

            if (teams != null) {
                for (TeamDTO team : teams) {
                    if (teamIds.contains(team.getId())) {
                        // Lấy báo cáo chi tiết cho tổ
                        List<ReportSupplyResponse> reportSupplyResponses =
                                supplyReplacementHistoryRepository.getSupplyReportByTeamAndDateRange(
                                        branchId, team.getId(), filter.getStartDate(), filter.getEndDate());

                        ReportDetailResponse reportDetailResponse = new ReportDetailResponse();
                        reportDetailResponse.setTeamName(team.getName());
                        reportDetailResponse.setTeamCode(team.getCode());
                        reportDetailResponse.setSupplies(
                                reportSupplyResponses != null ? reportSupplyResponses : Collections.emptyList()
                        );
                        teamReports.add(reportDetailResponse);

                        // ✅ Loại bỏ teamId đã xử lý
                        teamIds.remove(team.getId());
                    }
                }
            }

            // Chỉ add nếu có dữ liệu teamReports
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
}
