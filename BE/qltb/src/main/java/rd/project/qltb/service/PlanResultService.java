package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Acceptance;
import rd.project.qltb.domain.ErrorReport;
import rd.project.qltb.domain.PlanDetail;
import rd.project.qltb.domain.PlanResult;
import rd.project.qltb.domain.PlanResultDetail;
import rd.project.qltb.domain.SupplyReplacement;
import rd.project.qltb.model.PlanResultDTO;
import rd.project.qltb.repos.AcceptanceRepository;
import rd.project.qltb.repos.ErrorReportRepository;
import rd.project.qltb.repos.PlanDetailRepository;
import rd.project.qltb.repos.PlanResultDetailRepository;
import rd.project.qltb.repos.PlanResultRepository;
import rd.project.qltb.repos.SupplyReplacementRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class PlanResultService {

    private final PlanResultRepository planResultRepository;
    private final PlanDetailRepository planDetailRepository;
    private final PlanResultDetailRepository planResultDetailRepository;
    private final ErrorReportRepository errorReportRepository;
    private final AcceptanceRepository acceptanceRepository;
    private final SupplyReplacementRepository supplyReplacementRepository;

    public PlanResultService(final PlanResultRepository planResultRepository,
            final PlanDetailRepository planDetailRepository,
            final PlanResultDetailRepository planResultDetailRepository,
            final ErrorReportRepository errorReportRepository,
            final AcceptanceRepository acceptanceRepository,
            final SupplyReplacementRepository supplyReplacementRepository) {
        this.planResultRepository = planResultRepository;
        this.planDetailRepository = planDetailRepository;
        this.planResultDetailRepository = planResultDetailRepository;
        this.errorReportRepository = errorReportRepository;
        this.acceptanceRepository = acceptanceRepository;
        this.supplyReplacementRepository = supplyReplacementRepository;
    }

    public List<PlanResultDTO> findAll() {
        final List<PlanResult> planResults = planResultRepository.findAll(Sort.by("id"));
        return planResults.stream()
                .map(planResult -> mapToDTO(planResult, new PlanResultDTO()))
                .toList();
    }

    public PlanResultDTO get(final Long id) {
        return planResultRepository.findById(id)
                .map(planResult -> mapToDTO(planResult, new PlanResultDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanResultDTO planResultDTO) {
        final PlanResult planResult = new PlanResult();
        mapToEntity(planResultDTO, planResult);
        return planResultRepository.save(planResult).getId();
    }

    public void update(final Long id, final PlanResultDTO planResultDTO) {
        final PlanResult planResult = planResultRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planResultDTO, planResult);
        planResultRepository.save(planResult);
    }

    public void delete(final Long id) {
        planResultRepository.deleteById(id);
    }

    private PlanResultDTO mapToDTO(final PlanResult planResult, final PlanResultDTO planResultDTO) {
        planResultDTO.setId(planResult.getId());
        planResultDTO.setCode(planResult.getCode());
        planResultDTO.setNote(planResult.getNote());
        planResultDTO.setCreatedAt(planResult.getCreatedAt());
        planResultDTO.setUpdatedAt(planResult.getUpdatedAt());
        planResultDTO.setCreatedBy(planResult.getCreatedBy());
        planResultDTO.setStatus(planResult.getStatus());
        planResultDTO.setStatusRepair(planResult.getStatusRepair());
        planResultDTO.setPlanDetail(planResult.getPlanDetail() == null ? null : planResult.getPlanDetail().getId());
        planResultDTO.setPlanResultDetail(planResult.getPlanResultDetail() == null ? null : planResult.getPlanResultDetail().getId());
        return planResultDTO;
    }

    private PlanResult mapToEntity(final PlanResultDTO planResultDTO, final PlanResult planResult) {
        planResult.setCode(planResultDTO.getCode());
        planResult.setNote(planResultDTO.getNote());
        planResult.setCreatedAt(planResultDTO.getCreatedAt());
        planResult.setUpdatedAt(planResultDTO.getUpdatedAt());
        planResult.setCreatedBy(planResultDTO.getCreatedBy());
        planResult.setStatus(planResultDTO.getStatus());
        planResult.setStatusRepair(planResultDTO.getStatusRepair());
        final PlanDetail planDetail = planResultDTO.getPlanDetail() == null ? null : planDetailRepository.findById(planResultDTO.getPlanDetail())
                .orElseThrow(() -> new NotFoundException("planDetail not found"));
        planResult.setPlanDetail(planDetail);
        final PlanResultDetail planResultDetail = planResultDTO.getPlanResultDetail() == null ? null : planResultDetailRepository.findById(planResultDTO.getPlanResultDetail())
                .orElseThrow(() -> new NotFoundException("planResultDetail not found"));
        planResult.setPlanResultDetail(planResultDetail);
        return planResult;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final PlanResult planResult = planResultRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final ErrorReport planResultErrorReport = errorReportRepository.findFirstByPlanResult(planResult);
        if (planResultErrorReport != null) {
            referencedWarning.setKey("planResult.errorReport.planResult.referenced");
            referencedWarning.addParam(planResultErrorReport.getId());
            return referencedWarning;
        }
        final Acceptance planResultAcceptance = acceptanceRepository.findFirstByPlanResult(planResult);
        if (planResultAcceptance != null) {
            referencedWarning.setKey("planResult.acceptance.planResult.referenced");
            referencedWarning.addParam(planResultAcceptance.getId());
            return referencedWarning;
        }
        final SupplyReplacement planResultSupplyReplacement = supplyReplacementRepository.findFirstByPlanResult(planResult);
        if (planResultSupplyReplacement != null) {
            referencedWarning.setKey("planResult.supplyReplacement.planResult.referenced");
            referencedWarning.addParam(planResultSupplyReplacement.getId());
            return referencedWarning;
        }
        return null;
    }

}
