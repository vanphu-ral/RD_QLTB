package io.qltb.qltb.service;

import io.qltb.qltb.domain.Acceptance;
import io.qltb.qltb.domain.ErrorReport;
import io.qltb.qltb.domain.PlanResult;
import io.qltb.qltb.events.BeforeDeleteErrorReport;
import io.qltb.qltb.events.BeforeDeletePlanResult;
import io.qltb.qltb.model.AcceptanceDTO;
import io.qltb.qltb.repos.AcceptanceRepository;
import io.qltb.qltb.repos.ErrorReportRepository;
import io.qltb.qltb.repos.PlanResultRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
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
        final List<Acceptance> acceptances = acceptanceRepository.findAll(Sort.by("id"));
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
        acceptance.getPlanResult().setPlanResultDetail(null);
        acceptance.getPlanResult().setPlanResultSupplyReplacements(null);
        acceptance.getPlanResult().setPlanResultErrorReports(null);
        acceptance.getPlanResult().setPlanResultAcceptances(null);
        acceptance.getPlanResult().setPlanResultPlanResultDetails(null);
        acceptance.getErrorReport().setPlanResult(null);
        acceptance.getErrorReport().setErrorReportAcceptances(null);
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
        acceptanceDTO.setPlanResult(acceptance.getPlanResult() == null ? null : acceptance.getPlanResult());
        acceptanceDTO.setErrorReport(acceptance.getErrorReport() == null ? null : acceptance.getErrorReport());
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
