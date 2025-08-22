package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Acceptance;
import rd.project.qltb.domain.ErrorReport;
import rd.project.qltb.domain.PlanResult;
import rd.project.qltb.model.ErrorReportDTO;
import rd.project.qltb.repos.AcceptanceRepository;
import rd.project.qltb.repos.ErrorReportRepository;
import rd.project.qltb.repos.PlanResultRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class ErrorReportService {

    private final ErrorReportRepository errorReportRepository;
    private final PlanResultRepository planResultRepository;
    private final AcceptanceRepository acceptanceRepository;

    public ErrorReportService(final ErrorReportRepository errorReportRepository,
            final PlanResultRepository planResultRepository,
            final AcceptanceRepository acceptanceRepository) {
        this.errorReportRepository = errorReportRepository;
        this.planResultRepository = planResultRepository;
        this.acceptanceRepository = acceptanceRepository;
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
        errorReportRepository.deleteById(id);
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
        errorReport.setStatus(errorReportDTO.getStatus());
        final PlanResult planResult = errorReportDTO.getPlanResult() == null ? null : planResultRepository.findById(errorReportDTO.getPlanResult())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        errorReport.setPlanResult(planResult);
        return errorReport;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final ErrorReport errorReport = errorReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Acceptance errorReportAcceptance = acceptanceRepository.findFirstByErrorReport(errorReport);
        if (errorReportAcceptance != null) {
            referencedWarning.setKey("errorReport.acceptance.errorReport.referenced");
            referencedWarning.addParam(errorReportAcceptance.getId());
            return referencedWarning;
        }
        return null;
    }

}
