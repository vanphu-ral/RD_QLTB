package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Acceptance;
import rd.project.qltb.domain.ErrorReport;
import rd.project.qltb.domain.PlanResult;
import rd.project.qltb.model.AcceptanceDTO;
import rd.project.qltb.repos.AcceptanceRepository;
import rd.project.qltb.repos.ErrorReportRepository;
import rd.project.qltb.repos.PlanResultRepository;
import rd.project.qltb.util.NotFoundException;


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
        acceptanceRepository.deleteById(id);
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
        acceptanceDTO.setPlanResult(acceptance.getPlanResult() == null ? null : acceptance.getPlanResult().getId());
        acceptanceDTO.setErrorReport(acceptance.getErrorReport() == null ? null : acceptance.getErrorReport().getId());
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
        final PlanResult planResult = acceptanceDTO.getPlanResult() == null ? null : planResultRepository.findById(acceptanceDTO.getPlanResult())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        acceptance.setPlanResult(planResult);
        final ErrorReport errorReport = acceptanceDTO.getErrorReport() == null ? null : errorReportRepository.findById(acceptanceDTO.getErrorReport())
                .orElseThrow(() -> new NotFoundException("errorReport not found"));
        acceptance.setErrorReport(errorReport);
        return acceptance;
    }

}
