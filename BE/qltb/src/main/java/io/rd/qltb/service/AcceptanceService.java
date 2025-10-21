package io.rd.qltb.service;

import io.rd.qltb.domain.Acceptance;
import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.ErrorReport;
import io.rd.qltb.domain.PlanResult;
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

    public List<AcceptanceDTO> findAll() {
        final List<Acceptance> acceptances = acceptanceRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
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
        acceptanceRepository.delete(acceptance);
    }

    private AcceptanceDTO mapToDTO(final Acceptance acceptance, final AcceptanceDTO acceptanceDTO) {
        acceptanceDTO.setId(acceptance.getId());
        acceptanceDTO.setCode(acceptance.getCode());
        acceptanceDTO.setName(acceptance.getName());
        acceptanceDTO.setNote(acceptance.getNote());
        acceptanceDTO.setUser(acceptance.getUser());
        acceptanceDTO.setTimeAcceptance(acceptance.getTimeAcceptance());
        acceptanceDTO.setCreatedAt(acceptance.getCreatedAt());
        acceptanceDTO.setUpdatedAt(acceptance.getUpdatedAt());
        acceptanceDTO.setCreatedBy(acceptance.getCreatedBy());
        acceptanceDTO.setUpdatedBy(acceptance.getUpdatedBy());
        acceptanceDTO.setStatus(acceptance.getStatus());

        // Sao chép PlanResult có kiểm soát
        if (acceptance.getPlanResult() != null) {
            PlanResult planResultCopy = new PlanResult();
            planResultCopy.setId(acceptance.getPlanResult().getId());
            planResultCopy.setStatus(acceptance.getPlanResult().getStatus());
            planResultCopy.setCreatedAt(acceptance.getPlanResult().getCreatedAt());
            planResultCopy.setUpdatedAt(acceptance.getPlanResult().getUpdatedAt());
            planResultCopy.setCreatedBy(acceptance.getPlanResult().getCreatedBy());
            planResultCopy.setUpdatedBy(acceptance.getPlanResult().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp
            planResultCopy.setPlanResultSupplyReplacements(null);
            planResultCopy.setPlanResultErrorReports(null);
            planResultCopy.setPlanResultAcceptances(null);
            planResultCopy.setPlanResultPlanResultDetails(null);

            acceptanceDTO.setPlanResult(planResultCopy);
        } else {
            acceptanceDTO.setPlanResult(null);
        }

        // Sao chép ErrorReport có kiểm soát
        if (acceptance.getErrorReport() != null) {
            ErrorReport errorReportCopy = new ErrorReport();
            errorReportCopy.setId(acceptance.getErrorReport().getId());
            errorReportCopy.setCode(acceptance.getErrorReport().getCode());
            errorReportCopy.setStatus(acceptance.getErrorReport().getStatus());
            errorReportCopy.setCreatedAt(acceptance.getErrorReport().getCreatedAt());
            errorReportCopy.setUpdatedAt(acceptance.getErrorReport().getUpdatedAt());
            errorReportCopy.setCreatedBy(acceptance.getErrorReport().getCreatedBy());
            errorReportCopy.setUpdatedBy(acceptance.getErrorReport().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp
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
        acceptance.setNote(acceptanceDTO.getNote());
        acceptance.setUser(acceptanceDTO.getUser());
        acceptance.setTimeAcceptance(acceptanceDTO.getTimeAcceptance());
        acceptance.setCreatedAt(acceptanceDTO.getCreatedAt());
        acceptance.setUpdatedAt(acceptanceDTO.getUpdatedAt());
        acceptance.setCreatedBy(acceptanceDTO.getCreatedBy());
        acceptance.setUpdatedBy(acceptanceDTO.getUpdatedBy());
        acceptance.setStatus(acceptanceDTO.getStatus());
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

}
