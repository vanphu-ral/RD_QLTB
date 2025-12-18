package io.rd.qltb.service;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.ErrorReport;
import io.rd.qltb.domain.ReportDeviceIncident;
import io.rd.qltb.model.ReportDeviceIncidentDTO;
import io.rd.qltb.repos.ReportDeviceIncidentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.rd.qltb.config.GlobalConfig.DELETED;

@Service
public class ReportDeviceIncidentService {
    @Autowired
    ReportDeviceIncidentRepository reportDeviceIncidentRepository;
public ReportDeviceIncidentDTO mapToDTO(final ReportDeviceIncident reportDeviceIncident, final ReportDeviceIncidentDTO reportDeviceIncidentDTO) {
        reportDeviceIncidentDTO.setId(reportDeviceIncident.getId());
        reportDeviceIncidentDTO.setCode(reportDeviceIncident.getCode());
        reportDeviceIncidentDTO.setName(reportDeviceIncident.getName());
        reportDeviceIncidentDTO.setErrorDescription(reportDeviceIncident.getErrorDescription());
        reportDeviceIncidentDTO.setReason(reportDeviceIncident.getReason());
        reportDeviceIncidentDTO.setTreatmentMeasure(reportDeviceIncident.getTreatmentMeasure());
        reportDeviceIncidentDTO.setPerformer(reportDeviceIncident.getPerformer());
        reportDeviceIncidentDTO.setTimeComplete(reportDeviceIncident.getTimeComplete());
        reportDeviceIncidentDTO.setListUser(reportDeviceIncident.getListUser());
        reportDeviceIncidentDTO.setDivision(reportDeviceIncident.getDivision());
        reportDeviceIncidentDTO.setCreatedAt(reportDeviceIncident.getCreatedAt());
        reportDeviceIncidentDTO.setCreatedBy(reportDeviceIncident.getCreatedBy());
        reportDeviceIncidentDTO.setUpdatedAt(reportDeviceIncident.getUpdatedAt());
        reportDeviceIncidentDTO.setUpdatedBy(reportDeviceIncident.getUpdatedBy());
        reportDeviceIncidentDTO.setStatus(reportDeviceIncident.getStatus());
        reportDeviceIncidentDTO.setDocNumber(reportDeviceIncident.getDocNumber());
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
        entity.setDevice(dto.getDevice());
        return entity;
    }
    public ResponseEntity<?> createReportDeviceIncident(ReportDeviceIncidentDTO dto) {
        ReportDeviceIncident reportDeviceIncident = new ReportDeviceIncident();
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
    public List<ReportDeviceIncidentDTO> findAll(){
        List<ReportDeviceIncident> reportDeviceIncidents = reportDeviceIncidentRepository.findAllByStatusNotOrderByIdDesc(DELETED);
        return reportDeviceIncidents.stream().map(reportDeviceIncident -> mapToDTO(reportDeviceIncident, new ReportDeviceIncidentDTO())).toList();
    }
    public ReportDeviceIncidentDTO get(Long id){
        ReportDeviceIncident reportDeviceIncident = reportDeviceIncidentRepository.findById(id).orElseThrow();
        return mapToDTO(reportDeviceIncident, new ReportDeviceIncidentDTO());
    }
    public void deleteReportDeviceIncident(Long id) {

    ReportDeviceIncident reportDeviceIncident = reportDeviceIncidentRepository.findById(id).orElseThrow();
        reportDeviceIncident.setStatus(DELETED);
        reportDeviceIncidentRepository.save(reportDeviceIncident);
    }
}
