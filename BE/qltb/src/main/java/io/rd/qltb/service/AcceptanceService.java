package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeleteErrorReport;
import io.rd.qltb.events.BeforeDeletePlanResult;
import io.rd.qltb.model.AcceptanceDTO;
import io.rd.qltb.repos.AcceptanceRepository;
import io.rd.qltb.repos.ErrorReportRepository;
import io.rd.qltb.repos.PlanResultRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.ConstantStatusGlobal.DELETED;


@Service
public class AcceptanceService {

    private final AcceptanceRepository acceptanceRepository;
    private final PlanResultRepository planResultRepository;
    private final ErrorReportRepository errorReportRepository;

    public AcceptanceService(final AcceptanceRepository acceptanceRepository,
            final PlanResultRepository planResultRepository,
            final ErrorReportRepository errorReportRepository) {
        this.acceptanceRepository = acceptanceRepository;
        this.planResultRepository = planResultRepository;
        this.errorReportRepository = errorReportRepository;
    }
    public Integer checkIfExistByIdPlanDetail(Long id) {
        return acceptanceRepository.countByPlanDetailId(id);
    }

    public Integer checkIfExistByIdErrorReport(Long id) {
        return acceptanceRepository.countByErrorReportId(id);
    }
    public List<AcceptanceDTO> findAll() {
        final List<Acceptance> acceptances = acceptanceRepository.findAllByStatusNotOrderByIdDesc(DELETED);
        return acceptances.stream()
                .map(acceptance -> mapToDTO(acceptance, new AcceptanceDTO()))
                .toList();
    }

    public AcceptanceDTO get(final Long id) {
        return acceptanceRepository.findById(id)
                .map(acceptance -> mapToDTO(acceptance, new AcceptanceDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final AcceptanceDTO acceptanceDTO) {
        final Acceptance acceptance = new Acceptance();
        mapToEntity(acceptanceDTO, acceptance);
        return acceptanceRepository.save(acceptance).getId();
    }

    public void update(final Long id, final AcceptanceDTO acceptanceDTO) {
        final Acceptance acceptance = acceptanceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(acceptanceDTO, acceptance);
        acceptanceRepository.save(acceptance);
    }

    public void delete(final Long id) {
        final Acceptance acceptance = acceptanceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        acceptance.setStatus(DELETED);
        acceptanceRepository.save(acceptance);
    }

    private AcceptanceDTO mapToDTO(final Acceptance acceptance, final AcceptanceDTO acceptanceDTO) {
        acceptanceDTO.setId(acceptance.getId());
        acceptanceDTO.setCode(acceptance.getCode());
        acceptanceDTO.setName(acceptance.getName());
        acceptanceDTO.setType(acceptance.getType());
        acceptanceDTO.setNote(acceptance.getNote());
        acceptanceDTO.setUser(acceptance.getUser());
        acceptanceDTO.setResult(acceptance.getResult());
        acceptanceDTO.setSafe(acceptance.getSafe());
        acceptanceDTO.setQuality(acceptance.getQuality());
        acceptanceDTO.setProductivity(acceptance.getProductivity());
        acceptanceDTO.setActionMore(acceptance.getActionMore());
        acceptanceDTO.setResponsibility(acceptance.getResponsibility());
        acceptanceDTO.setLimitation(acceptance.getLimitation());
        acceptanceDTO.setFromDateAcceptance(acceptance.getFromDateAcceptance());
        acceptanceDTO.setToDateAcceptance(acceptance.getToDateAcceptance());
        acceptanceDTO.setFromDatePerform(acceptance.getFromDatePerform());
        acceptanceDTO.setToDatePerform(acceptance.getToDatePerform());
        acceptanceDTO.setTimeAcceptance(acceptance.getTimeAcceptance());
        acceptanceDTO.setPlanDetailId(acceptance.getPlanDetailId());
        acceptanceDTO.setCreatedAt(acceptance.getCreatedAt());
        acceptanceDTO.setUpdatedAt(acceptance.getUpdatedAt());
        acceptanceDTO.setCreatedBy(acceptance.getCreatedBy());
        acceptanceDTO.setUpdatedBy(acceptance.getUpdatedBy());
        acceptanceDTO.setStatus(acceptance.getStatus());
        acceptanceDTO.setDocNumber(acceptance.getDocNumber());
        acceptanceDTO.setImplementingUnit(acceptance.getImplementingUnit());
        acceptanceDTO.setDateRecord(acceptance.getDateRecord());
        acceptanceDTO.setFormCode(acceptance.getFormCode());
        acceptanceDTO.setNumberOfIssuances(acceptance.getNumberOfIssuances());
        acceptanceDTO.setIncurredWork(acceptance.getIncurredWork());
        if (acceptance.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(acceptance.getDevice().getId());
            deviceCopy.setCode(acceptance.getDevice().getCode());
            deviceCopy.setName(acceptance.getDevice().getName());
            deviceCopy.setSerialNumber(acceptance.getDevice().getSerialNumber());
            deviceCopy.setUnit(acceptance.getDevice().getUnit());
            deviceCopy.setStatus(acceptance.getDevice().getStatus());
            deviceCopy.setCreatedAt(acceptance.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(acceptance.getDevice().getUpdatedAt());

            // XÃ³a cÃ¡c quan há»‡ con
            deviceCopy.setGroup(acceptance.getDevice().getGroup());
            deviceCopy.getGroup().setGroupDevices(null);
            deviceCopy.getGroup().setDeviceGroupSampleReports(null);
            deviceCopy.getGroup().setDeviceGroupKeyMappingDeviceSampleReports(null);
            deviceCopy.getGroup().setDeviceGroupPlanDetails(null);

            if(acceptance.getDevice().getLine() != null) {
                deviceCopy.setLine(acceptance.getDevice().getLine());
                deviceCopy.getLine().setLineDevices(null);
                deviceCopy.getLine().setTeam(null);
            }

            deviceCopy.setBranch(acceptance.getDevice().getBranch());
            deviceCopy.getBranch().getFactory().setFactoryBranches(null);
            deviceCopy.getBranch().setBranchDevices(null);
            deviceCopy.getBranch().setBranchTeams(null);
            deviceCopy.getBranch().setSampleReports(null);

            deviceCopy.setTeam(acceptance.getDevice().getTeam());
            deviceCopy.getTeam().setTeamDevices(null);
            deviceCopy.getTeam().setBranch(null);
            deviceCopy.getTeam().setTeamLines(null);

            deviceCopy.setDeviceDeviceParameterUses(null);
            deviceCopy.setDeviceDeviceRelocationHistories(null);
            deviceCopy.setDeviceDeviceSupplyUsages(null);
            deviceCopy.setDevicePlanDetails(null);
            acceptanceDTO.setDevice(deviceCopy);
        } else {
            acceptanceDTO.setDevice(null);
        }
        // Sao chÃ©p PlanResult cÃ³ kiá»ƒm soÃ¡t
        if (acceptance.getPlanResult() != null) {
            PlanResult planResultCopy = new PlanResult();
            planResultCopy.setId(acceptance.getPlanResult().getId());
            planResultCopy.setStatus(acceptance.getPlanResult().getStatus());
            planResultCopy.setCreatedAt(acceptance.getPlanResult().getCreatedAt());
            planResultCopy.setUpdatedAt(acceptance.getPlanResult().getUpdatedAt());
            planResultCopy.setCreatedBy(acceptance.getPlanResult().getCreatedBy());
            planResultCopy.setUpdatedBy(acceptance.getPlanResult().getUpdatedBy());

            // XÃ³a cÃ¡c quan há»‡ con Ä‘á»ƒ trÃ¡nh vÃ²ng láº·p
            planResultCopy.setPlanResultSupplyReplacements(null);
            planResultCopy.setPlanResultErrorReports(null);
            planResultCopy.setPlanResultAcceptances(null);
            planResultCopy.setPlanResultPlanResultDetails(null);

            acceptanceDTO.setPlanResult(planResultCopy);
        } else {
            acceptanceDTO.setPlanResult(null);
        }

        // Sao chÃ©p ErrorReport cÃ³ kiá»ƒm soÃ¡t
        if (acceptance.getErrorReport() != null) {
            ErrorReport errorReportCopy = new ErrorReport();
            errorReportCopy.setId(acceptance.getErrorReport().getId());
            errorReportCopy.setCode(acceptance.getErrorReport().getCode());
            errorReportCopy.setStatus(acceptance.getErrorReport().getStatus());
            errorReportCopy.setCreatedAt(acceptance.getErrorReport().getCreatedAt());
            errorReportCopy.setUpdatedAt(acceptance.getErrorReport().getUpdatedAt());
            errorReportCopy.setCreatedBy(acceptance.getErrorReport().getCreatedBy());
            errorReportCopy.setUpdatedBy(acceptance.getErrorReport().getUpdatedBy());

            // XÃ³a cÃ¡c quan há»‡ con Ä‘á»ƒ trÃ¡nh vÃ²ng láº·p
            errorReportCopy.setPlanResult(null);
            errorReportCopy.setErrorReportAcceptances(null);

            acceptanceDTO.setErrorReport(errorReportCopy);
        } else {
            acceptanceDTO.setErrorReport(null);
        }
        if(acceptance.getApprovalWorkflow() != null) {
            ApprovalWorkflow approvalWorkflowCopy = new ApprovalWorkflow();
            approvalWorkflowCopy.setId(acceptance.getApprovalWorkflow().getId());
            approvalWorkflowCopy.setCode(acceptance.getApprovalWorkflow().getCode());
            approvalWorkflowCopy.setName(acceptance.getApprovalWorkflow().getName());
            approvalWorkflowCopy.setStatus(acceptance.getApprovalWorkflow().getStatus());
        }

        return acceptanceDTO;
    }


    private Acceptance mapToEntity(final AcceptanceDTO acceptanceDTO, final Acceptance acceptance) {
        acceptance.setCode(acceptanceDTO.getCode());
        acceptance.setName(acceptanceDTO.getName());
        acceptance.setType(acceptanceDTO.getType());
        acceptance.setNote(acceptanceDTO.getNote());
        acceptance.setUser(acceptanceDTO.getUser());
        acceptance.setResult(acceptanceDTO.getResult());
        acceptance.setSafe(acceptanceDTO.getSafe());
        acceptance.setQuality(acceptanceDTO.getQuality());
        acceptance.setProductivity(acceptanceDTO.getProductivity());
        acceptance.setActionMore(acceptanceDTO.getActionMore());
        acceptance.setResponsibility(acceptanceDTO.getResponsibility());
        acceptance.setLimitation(acceptanceDTO.getLimitation());
        acceptance.setFromDateAcceptance(acceptanceDTO.getFromDateAcceptance());
        acceptance.setToDateAcceptance(acceptanceDTO.getToDateAcceptance());
        acceptance.setFromDatePerform(acceptanceDTO.getFromDatePerform());
        acceptance.setToDatePerform(acceptanceDTO.getToDatePerform());
        acceptance.setTimeAcceptance(acceptanceDTO.getTimeAcceptance());
        acceptance.setPlanDetailId(acceptanceDTO.getPlanDetailId());
        acceptance.setCreatedAt(acceptanceDTO.getCreatedAt());
        acceptance.setUpdatedAt(acceptanceDTO.getUpdatedAt());
        acceptance.setCreatedBy(acceptanceDTO.getCreatedBy());
        acceptance.setUpdatedBy(acceptanceDTO.getUpdatedBy());
        acceptance.setStatus(acceptanceDTO.getStatus());
        acceptance.setDocNumber(acceptanceDTO.getDocNumber());
        acceptance.setDevice(acceptanceDTO.getDevice());
        acceptance.setImplementingUnit(acceptanceDTO.getImplementingUnit());
        acceptance.setDateRecord(acceptanceDTO.getDateRecord());
        acceptance.setFormCode(acceptanceDTO.getFormCode());
        acceptance.setNumberOfIssuances(acceptanceDTO.getNumberOfIssuances());
        acceptance.setIncurredWork(acceptanceDTO.getIncurredWork());
        final PlanResult planResult = acceptanceDTO.getPlanResult() == null ? null : planResultRepository.findById(acceptanceDTO.getPlanResult().getId())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        acceptance.setPlanResult(planResult);
        final ErrorReport errorReport = acceptanceDTO.getErrorReport() == null ? null : errorReportRepository.findById(acceptanceDTO.getErrorReport().getId())
                .orElseThrow(() -> new NotFoundException("errorReport not found"));
        acceptance.setErrorReport(errorReport);
        return acceptance;
    }

    @EventListener(BeforeDeletePlanResult.class)
    public void on(final BeforeDeletePlanResult event) {
        final ReferencedException referencedException = new ReferencedException();
        final Acceptance planResultAcceptance = acceptanceRepository.findFirstByPlanResultId(event.getId());
        if (planResultAcceptance != null) {
            referencedException.setKey("planResult.acceptance.planResult.referenced");
            referencedException.addParam(planResultAcceptance.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteErrorReport.class)
    public void on(final BeforeDeleteErrorReport event) {
        final ReferencedException referencedException = new ReferencedException();
        final Acceptance errorReportAcceptance = acceptanceRepository.findFirstByErrorReportId(event.getId());
        if (errorReportAcceptance != null) {
            referencedException.setKey("errorReport.acceptance.errorReport.referenced");
            referencedException.addParam(errorReportAcceptance.getId());
            throw referencedException;
        }
    }


    public java.util.Map<String, Object> getLatestApprovedFormConfig() {
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        java.util.Optional<Acceptance> latest = acceptanceRepository.findFirstByStatusAndFormCodeIsNotNullOrderByIdDesc(io.rd.qltb.config.ConstantStatusGlobal.APPROVED);
        if (latest.isPresent() && latest.get().getFormCode() != null && !latest.get().getFormCode().trim().isEmpty()) {
            result.put("formCode", latest.get().getFormCode().trim());
            result.put("numberOfIssuances", latest.get().getNumberOfIssuances() != null ? latest.get().getNumberOfIssuances() : 1);
        } else {
            result.put("formCode", "RĐ.QT15.BM07");
            result.put("numberOfIssuances", 1);
        }
        return result;
    }

    @org.springframework.beans.factory.annotation.Autowired
    private io.rd.qltb.repos.PlanDetailRepository planDetailRepository;

    public List<java.util.Map<String, Object>> getUnrecordedCompletedPlanDetails(String branch) {
        String filterBranch = (branch != null && !branch.trim().isEmpty()) ? branch.trim() : null;
        List<PlanDetail> list = planDetailRepository.findUnrecordedCompletedPlanDetails(filterBranch);
        return list.stream().map(pd -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", pd.getId());
            map.put("status", pd.getStatus());
            map.put("updatedAt", pd.getUpdatedAt());
            map.put("createdAt", pd.getCreatedAt());
            map.put("nameDetail", pd.getNameDetail());
            map.put("manager", pd.getManager());

            if (pd.getDevice() != null) {
                Device dev = pd.getDevice();
                java.util.Map<String, Object> devMap = new java.util.HashMap<>();
                devMap.put("id", dev.getId());
                devMap.put("code", dev.getCode());
                devMap.put("name", dev.getName());
                if (dev.getBranch() != null) {
                    java.util.Map<String, Object> bMap = new java.util.HashMap<>();
                    bMap.put("id", dev.getBranch().getId());
                    bMap.put("name", dev.getBranch().getName());
                    devMap.put("branch", bMap);
                }
                if (dev.getLine() != null) {
                    java.util.Map<String, Object> lMap = new java.util.HashMap<>();
                    lMap.put("id", dev.getLine().getId());
                    lMap.put("name", dev.getLine().getName());
                    devMap.put("line", lMap);
                }
                if (dev.getTeam() != null) {
                    java.util.Map<String, Object> tMap = new java.util.HashMap<>();
                    tMap.put("id", dev.getTeam().getId());
                    tMap.put("name", dev.getTeam().getName());
                    devMap.put("team", tMap);
                }
                map.put("device", devMap);
            }
            if (pd.getSampleReport() != null) {
                SampleReport sr = pd.getSampleReport();
                java.util.Map<String, Object> srMap = new java.util.HashMap<>();
                srMap.put("id", sr.getId());
                srMap.put("code", sr.getCode());
                srMap.put("name", sr.getName());
                map.put("sampleReport", srMap);
            }
            if (pd.getPlan() != null) {
                Plan p = pd.getPlan();
                java.util.Map<String, Object> pMap = new java.util.HashMap<>();
                pMap.put("id", p.getId());
                pMap.put("code", p.getCode());
                pMap.put("name", p.getName());
                pMap.put("fromDate", p.getFromDate());
                pMap.put("toDate", p.getToDate());
                if (p.getPlanType() != null) {
                    java.util.Map<String, Object> ptMap = new java.util.HashMap<>();
                    ptMap.put("id", p.getPlanType().getId());
                    ptMap.put("code", p.getPlanType().getCode());
                    ptMap.put("name", p.getPlanType().getName());
                    pMap.put("planType", ptMap);
                }
                map.put("plan", pMap);
            }
            return map;
        }).toList();
    }
}