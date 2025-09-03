package io.qltb.qltb.service;

import io.qltb.qltb.domain.ErrorReport;
import io.qltb.qltb.domain.PlanResult;
import io.qltb.qltb.events.BeforeDeleteErrorReport;
import io.qltb.qltb.events.BeforeDeletePlanResult;
import io.qltb.qltb.model.ErrorReportDTO;
import io.qltb.qltb.repos.ErrorReportRepository;
import io.qltb.qltb.repos.PlanResultRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ErrorReportService {

    private final ErrorReportRepository errorReportRepository;
    private final PlanResultRepository planResultRepository;
    private final ApplicationEventPublisher publisher;

    public ErrorReportService(final ErrorReportRepository errorReportRepository,
            final PlanResultRepository planResultRepository,
            final ApplicationEventPublisher publisher) {
        this.errorReportRepository = errorReportRepository;
        this.planResultRepository = planResultRepository;
        this.publisher = publisher;
    }

    public List<ErrorReportDTO> findAll() {
        final List<ErrorReport> errorReports = errorReportRepository.findAll(Sort.by("id"));
        return errorReports.stream()
                .map(errorReport -> mapToDTO(errorReport, new ErrorReportDTO()))
                .toList();
    }

    public ErrorReportDTO get(final Long id) {
        return errorReportRepository.findById(id)
                .map(errorReport -> mapToDTO(errorReport, new ErrorReportDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ErrorReportDTO errorReportDTO) {
        final ErrorReport errorReport = new ErrorReport();
        mapToEntity(errorReportDTO, errorReport);
        return errorReportRepository.save(errorReport).getId();
    }

    public void update(final Long id, final ErrorReportDTO errorReportDTO) {
        final ErrorReport errorReport = errorReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(errorReportDTO, errorReport);
        errorReportRepository.save(errorReport);
    }

    public void delete(final Long id) {
        final ErrorReport errorReport = errorReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteErrorReport(id));
        errorReportRepository.delete(errorReport);
    }

    private ErrorReportDTO mapToDTO(final ErrorReport errorReport,
            final ErrorReportDTO errorReportDTO) {
        errorReportDTO.setId(errorReport.getId());
        errorReportDTO.setCode(errorReport.getCode());
        errorReportDTO.setSeverity(errorReport.getSeverity());
        errorReportDTO.setErrorDescription(errorReport.getErrorDescription());
        errorReportDTO.setReportedBy(errorReport.getReportedBy());
        errorReportDTO.setTimeReported(errorReport.getTimeReported());
        errorReportDTO.setIsRepaired(errorReport.getIsRepaired());
        errorReportDTO.setRepairDescription(errorReport.getRepairDescription());
        errorReportDTO.setRepairedBy(errorReport.getRepairedBy());
        errorReportDTO.setTimeRepaired(errorReport.getTimeRepaired());
        errorReportDTO.setUser(errorReport.getUser());
        errorReportDTO.setCreatedAt(errorReport.getCreatedAt());
        errorReportDTO.setUpdatedAt(errorReport.getUpdatedAt());
        errorReportDTO.setCreatedBy(errorReport.getCreatedBy());
        errorReportDTO.setUpdatedBy(errorReport.getUpdatedBy());
        errorReportDTO.setStatus(errorReport.getStatus());
        errorReportDTO.setPlanResult(errorReport.getPlanResult() == null ? null : errorReport.getPlanResult().getId());
        return errorReportDTO;
    }

    private ErrorReport mapToEntity(final ErrorReportDTO errorReportDTO,
            final ErrorReport errorReport) {
        errorReport.setCode(errorReportDTO.getCode());
        errorReport.setSeverity(errorReportDTO.getSeverity());
        errorReport.setErrorDescription(errorReportDTO.getErrorDescription());
        errorReport.setReportedBy(errorReportDTO.getReportedBy());
        errorReport.setTimeReported(errorReportDTO.getTimeReported());
        errorReport.setIsRepaired(errorReportDTO.getIsRepaired());
        errorReport.setRepairDescription(errorReportDTO.getRepairDescription());
        errorReport.setRepairedBy(errorReportDTO.getRepairedBy());
        errorReport.setTimeRepaired(errorReportDTO.getTimeRepaired());
        errorReport.setUser(errorReportDTO.getUser());
        errorReport.setCreatedAt(errorReportDTO.getCreatedAt());
        errorReport.setUpdatedAt(errorReportDTO.getUpdatedAt());
        errorReport.setCreatedBy(errorReportDTO.getCreatedBy());
        errorReport.setUpdatedBy(errorReportDTO.getUpdatedBy());
        errorReport.setStatus(errorReportDTO.getStatus());
        final PlanResult planResult = errorReportDTO.getPlanResult() == null ? null : planResultRepository.findById(errorReportDTO.getPlanResult())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        errorReport.setPlanResult(planResult);
        return errorReport;
    }

    @EventListener(BeforeDeletePlanResult.class)
    public void on(final BeforeDeletePlanResult event) {
        final ReferencedException referencedException = new ReferencedException();
        final ErrorReport planResultErrorReport = errorReportRepository.findFirstByPlanResultId(event.getId());
        if (planResultErrorReport != null) {
            referencedException.setKey("planResult.errorReport.planResult.referenced");
            referencedException.addParam(planResultErrorReport.getId());
            throw referencedException;
        }
    }

}
