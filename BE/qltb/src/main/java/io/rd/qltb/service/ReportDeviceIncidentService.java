package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.ErrorReport;
import io.rd.qltb.domain.PlanDetail;
import io.rd.qltb.domain.ReportDeviceIncident;
import io.rd.qltb.model.ReportDeviceIncidentDTO;
import io.rd.qltb.repos.ErrorReportRepository;
import io.rd.qltb.repos.ReportDeviceIncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.rd.qltb.config.ConstantStatusGlobal.DELETED;

@Service
public class ReportDeviceIncidentService {
    @Autowired
    ReportDeviceIncidentRepository reportDeviceIncidentRepository;
    @Autowired
    ErrorReportRepository errorReportRepository;

    public ReportDeviceIncidentDTO mapToDTO(final ReportDeviceIncident reportDeviceIncident, final ReportDeviceIncidentDTO reportDeviceIncidentDTO) {
        reportDeviceIncidentDTO.setId(reportDeviceIncident.getId());
        reportDeviceIncidentDTO.setCode(reportDeviceIncident.getCode());
        reportDeviceIncidentDTO.setName(reportDeviceIncident.getName());
        reportDeviceIncidentDTO.setErrorDescription(reportDeviceIncident.getErrorDescription());
        reportDeviceIncidentDTO.setReason(reportDeviceIncident.getReason());
        reportDeviceIncidentDTO.setTreatmentMeasure(reportDeviceIncident.getTreatmentMeasure());
        reportDeviceIncidentDTO.setPerformer(reportDeviceIncident.getPerformer());
        reportDeviceIncidentDTO.setExecutingDepartment(reportDeviceIncident.getExecutingDepartment());
        reportDeviceIncidentDTO.setTimeComplete(reportDeviceIncident.getTimeComplete());
        reportDeviceIncidentDTO.setListUser(reportDeviceIncident.getListUser());
        reportDeviceIncidentDTO.setDivision(reportDeviceIncident.getDivision());
        reportDeviceIncidentDTO.setCreatedAt(reportDeviceIncident.getCreatedAt());
        reportDeviceIncidentDTO.setCreatedBy(reportDeviceIncident.getCreatedBy());
        reportDeviceIncidentDTO.setUpdatedAt(reportDeviceIncident.getUpdatedAt());
        reportDeviceIncidentDTO.setUpdatedBy(reportDeviceIncident.getUpdatedBy());
        reportDeviceIncidentDTO.setStatus(reportDeviceIncident.getStatus());
        reportDeviceIncidentDTO.setDocNumber(reportDeviceIncident.getDocNumber());
        reportDeviceIncidentDTO.setFormCode(reportDeviceIncident.getFormCode());
        reportDeviceIncidentDTO.setNumberOfIssuances(reportDeviceIncident.getNumberOfIssuances());
        if (reportDeviceIncident.getWorkflow() != null) {
            ApprovalWorkflow workflowCopy = new ApprovalWorkflow();
            workflowCopy.setId(reportDeviceIncident.getWorkflow().getId());
            workflowCopy.setCode(reportDeviceIncident.getWorkflow().getCode());
            workflowCopy.setName(reportDeviceIncident.getWorkflow().getName());
            workflowCopy.setDescription(reportDeviceIncident.getWorkflow().getDescription());
            workflowCopy.setCreatedAt(reportDeviceIncident.getWorkflow().getCreatedAt());
            workflowCopy.setUpdatedAt(reportDeviceIncident.getWorkflow().getUpdatedAt());
            workflowCopy.setCreatedBy(reportDeviceIncident.getWorkflow().getCreatedBy());
            workflowCopy.setUpdatedBy(reportDeviceIncident.getWorkflow().getUpdatedBy());
            workflowCopy.setStatus(reportDeviceIncident.getWorkflow().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            workflowCopy.setWorkflowApprovalGroups(null);
            workflowCopy.setWorkflowSampleReports(null);

            reportDeviceIncidentDTO.setWorkflow(workflowCopy);
        } else {
            reportDeviceIncidentDTO.setWorkflow(null);
        }
        if (reportDeviceIncident.getErrorReport() != null) {
            ErrorReport errorReportCopy = new ErrorReport();
            errorReportCopy.setId(reportDeviceIncident.getErrorReport().getId());
            errorReportCopy.setCode(reportDeviceIncident.getErrorReport().getCode());
            errorReportCopy.setName(reportDeviceIncident.getErrorReport().getName());
            errorReportCopy.setSeverity(reportDeviceIncident.getErrorReport().getSeverity());
            errorReportCopy.setErrorDescription(reportDeviceIncident.getErrorReport().getErrorDescription());
            errorReportCopy.setReportedBy(reportDeviceIncident.getErrorReport().getReportedBy());
            errorReportCopy.setTimeReported(reportDeviceIncident.getErrorReport().getTimeReported());
            errorReportCopy.setIsRepaired(reportDeviceIncident.getErrorReport().getIsRepaired());
            errorReportCopy.setRepairDescription(reportDeviceIncident.getErrorReport().getRepairDescription());
            errorReportCopy.setRepairedBy(reportDeviceIncident.getErrorReport().getRepairedBy());
            errorReportCopy.setResult(reportDeviceIncident.getErrorReport().getResult());
            errorReportCopy.setTimeRepaired(reportDeviceIncident.getErrorReport().getTimeRepaired());
            errorReportCopy.setUser(reportDeviceIncident.getErrorReport().getUser());
            errorReportCopy.setCreatedAt(reportDeviceIncident.getErrorReport().getCreatedAt());
            errorReportCopy.setCreatedBy(reportDeviceIncident.getErrorReport().getCreatedBy());
            errorReportCopy.setUpdatedAt(reportDeviceIncident.getErrorReport().getUpdatedAt());
            errorReportCopy.setUpdatedBy(reportDeviceIncident.getErrorReport().getUpdatedBy());
            errorReportCopy.setStatus(reportDeviceIncident.getErrorReport().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            errorReportCopy.setPlanResult(null);
            errorReportCopy.setErrorReportAcceptances(null);

            reportDeviceIncidentDTO.setErrorReport(errorReportCopy);
        } else {
            reportDeviceIncidentDTO.setErrorReport(null);
        }
        if (reportDeviceIncident.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(reportDeviceIncident.getDevice().getId());
            deviceCopy.setCode(reportDeviceIncident.getDevice().getCode());
            deviceCopy.setName(reportDeviceIncident.getDevice().getName());
            deviceCopy.setSerialNumber(reportDeviceIncident.getDevice().getSerialNumber());
            deviceCopy.setUnit(reportDeviceIncident.getDevice().getUnit());
            deviceCopy.setStatus(reportDeviceIncident.getDevice().getStatus());
            deviceCopy.setCreatedAt(reportDeviceIncident.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(reportDeviceIncident.getDevice().getUpdatedAt());

            // Xóa các quan hệ con
            deviceCopy.setGroup(reportDeviceIncident.getDevice().getGroup());
            deviceCopy.getGroup().setGroupDevices(null);
            deviceCopy.getGroup().setDeviceGroupSampleReports(null);
            deviceCopy.getGroup().setDeviceGroupKeyMappingDeviceSampleReports(null);
            deviceCopy.getGroup().setDeviceGroupPlanDetails(null);

            deviceCopy.setLine(reportDeviceIncident.getDevice().getLine());
            deviceCopy.getLine().setLineDevices(null);
            deviceCopy.getLine().setTeam(null);

            deviceCopy.setBranch(reportDeviceIncident.getDevice().getBranch());
            deviceCopy.getBranch().getFactory().setFactoryBranches(null);
            deviceCopy.getBranch().setBranchDevices(null);
            deviceCopy.getBranch().setBranchTeams(null);
            deviceCopy.getBranch().setSampleReports(null);

            deviceCopy.setTeam(reportDeviceIncident.getDevice().getTeam());
            deviceCopy.getTeam().setTeamDevices(null);
            deviceCopy.getTeam().setBranch(null);
            deviceCopy.getTeam().setTeamLines(null);

            deviceCopy.setDeviceDeviceParameterUses(null);
            deviceCopy.setDeviceDeviceRelocationHistories(null);
            deviceCopy.setDeviceDeviceSupplyUsages(null);
            deviceCopy.setDevicePlanDetails(null);
            reportDeviceIncidentDTO.setDevice(deviceCopy);
        } else {
            reportDeviceIncidentDTO.setDevice(null);
        }
        return reportDeviceIncidentDTO;
    }

    public ReportDeviceIncident mapToEntity(final ReportDeviceIncidentDTO dto, final ReportDeviceIncident entity) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setErrorDescription(dto.getErrorDescription());
        entity.setReason(dto.getReason());
        entity.setTreatmentMeasure(dto.getTreatmentMeasure());
        entity.setPerformer(dto.getPerformer());
        entity.setExecutingDepartment(dto.getExecutingDepartment());
        entity.setTimeComplete(dto.getTimeComplete());
        entity.setListUser(dto.getListUser());
        entity.setDivision(dto.getDivision());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setUpdatedBy(dto.getUpdatedBy());
        entity.setStatus(dto.getStatus());
        entity.setWorkflow(dto.getWorkflow());
        entity.setErrorReport(dto.getErrorReport());
        entity.setDocNumber(dto.getDocNumber());
        entity.setFormCode(dto.getFormCode());
        entity.setNumberOfIssuances(dto.getNumberOfIssuances());
        entity.setDevice(dto.getDevice());
        return entity;
    }

    public ResponseEntity<?> createReportDeviceIncident(ReportDeviceIncidentDTO dto) {
        ReportDeviceIncident reportDeviceIncident = null;
        if (dto.getErrorReport() != null && dto.getErrorReport().getId() != null) {
            reportDeviceIncident = reportDeviceIncidentRepository.findByErrorReportId(dto.getErrorReport().getId()).orElse(null);
        }
        if (reportDeviceIncident == null) {
            reportDeviceIncident = new ReportDeviceIncident();
        }
        mapToEntity(dto, reportDeviceIncident);
        reportDeviceIncident = reportDeviceIncidentRepository.save(reportDeviceIncident);
        return ResponseEntity.ok(reportDeviceIncident.getId());
    }

    public ResponseEntity<?> updateReportDeviceIncident(Long id, ReportDeviceIncidentDTO dto) {
        ReportDeviceIncident reportDeviceIncident = reportDeviceIncidentRepository.findById(id).orElseThrow();
        mapToEntity(dto, reportDeviceIncident);
        reportDeviceIncidentRepository.save(reportDeviceIncident);
        return ResponseEntity.ok(id);
    }

    public Integer checkIfExistByErrorReportId(Long errorReportId) {
        return reportDeviceIncidentRepository.findByErrorReportId(errorReportId).isPresent() ? 1 : 0;
    }

    public List<ReportDeviceIncidentDTO> findAll() {
        List<ReportDeviceIncident> reportDeviceIncidents = reportDeviceIncidentRepository.findAllByStatusNotOrderByIdDesc(DELETED);
        return reportDeviceIncidents.stream().map(reportDeviceIncident -> mapToDTO(reportDeviceIncident, new ReportDeviceIncidentDTO())).toList();
    }

    public ReportDeviceIncidentDTO get(Long id) {
        ReportDeviceIncident reportDeviceIncident = reportDeviceIncidentRepository.findById(id).orElseThrow();
        return mapToDTO(reportDeviceIncident, new ReportDeviceIncidentDTO());
    }

    public void deleteReportDeviceIncident(Long id) {
        ReportDeviceIncident reportDeviceIncident = reportDeviceIncidentRepository.findById(id).orElseThrow();
        reportDeviceIncident.setStatus(DELETED);
        reportDeviceIncidentRepository.save(reportDeviceIncident);
    }

    public Map<String, Object> getLatestApprovedFormConfig() {
        Map<String, Object> result = new HashMap<>();
        Optional<ReportDeviceIncident> latest = reportDeviceIncidentRepository.findFirstByStatusAndFormCodeIsNotNullOrderByIdDesc(io.rd.qltb.config.ConstantStatusGlobal.APPROVED);
        if (latest.isPresent() && latest.get().getFormCode() != null && !latest.get().getFormCode().trim().isEmpty()) {
            result.put("formCode", latest.get().getFormCode().trim());
            result.put("numberOfIssuances", latest.get().getNumberOfIssuances() != null ? latest.get().getNumberOfIssuances() : 1);
        } else {
            result.put("formCode", "RĐ.QT15.BM06");
            result.put("numberOfIssuances", 1);
        }
        return result;
    }

    public List<Map<String, Object>> getUnrecordedSeriousErrors(String branch) {
        String filterBranch = (branch != null && !branch.trim().isEmpty()) ? branch.trim() : null;
        List<ErrorReport> errors = errorReportRepository.findUnrecordedSeriousErrors(filterBranch);
        return errors.stream().map(er -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", er.getId());
            map.put("code", er.getCode());
            map.put("name", er.getName());
            map.put("severity", er.getSeverity());
            map.put("errorDescription", er.getErrorDescription());
            map.put("reportedBy", er.getReportedBy());
            map.put("timeReported", er.getTimeReported());
            map.put("isRepaired", er.getIsRepaired());
            map.put("repairedBy", er.getRepairedBy());
            map.put("timeRepaired", er.getTimeRepaired());
            map.put("result", er.getResult());
            map.put("repairDescription", er.getRepairDescription());

            Device dev = null;
            PlanDetail pd = null;
            if (er.getPlanResult() != null && er.getPlanResult().getPlanDetail() != null) {
                pd = er.getPlanResult().getPlanDetail();
                dev = pd.getDevice();
            }
            if (dev != null) {
                Map<String, Object> devMap = new HashMap<>();
                devMap.put("id", dev.getId());
                devMap.put("code", dev.getCode());
                devMap.put("name", dev.getName());
                if (dev.getBranch() != null) {
                    Map<String, Object> bMap = new HashMap<>();
                    bMap.put("id", dev.getBranch().getId());
                    bMap.put("name", dev.getBranch().getName());
                    devMap.put("branch", bMap);
                }
                if (dev.getLine() != null) {
                    Map<String, Object> lMap = new HashMap<>();
                    lMap.put("id", dev.getLine().getId());
                    lMap.put("name", dev.getLine().getName());
                    devMap.put("line", lMap);
                }
                if (dev.getTeam() != null) {
                    Map<String, Object> tMap = new HashMap<>();
                    tMap.put("id", dev.getTeam().getId());
                    tMap.put("name", dev.getTeam().getName());
                    devMap.put("team", tMap);
                }
                map.put("device", devMap);
            }
            if (pd != null) {
                Map<String, Object> pdMap = new HashMap<>();
                pdMap.put("id", pd.getId());
                pdMap.put("device", map.get("device"));
                if (pd.getPlan() != null) {
                    Map<String, Object> pMap = new HashMap<>();
                    pMap.put("id", pd.getPlan().getId());
                    pMap.put("code", pd.getPlan().getCode());
                    pMap.put("name", pd.getPlan().getName());
                    pdMap.put("plan", pMap);
                }
                map.put("planDetail", pdMap);
            }
            return map;
        }).toList();
    }
}
