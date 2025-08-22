package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.PlanTarget;
import rd.project.qltb.domain.PlanTargetResult;
import rd.project.qltb.model.PlanTargetResultDTO;
import rd.project.qltb.repos.PlanTargetRepository;
import rd.project.qltb.repos.PlanTargetResultRepository;
import rd.project.qltb.util.NotFoundException;


@Service
public class PlanTargetResultService {

    private final PlanTargetResultRepository planTargetResultRepository;
    private final PlanTargetRepository planTargetRepository;

    public PlanTargetResultService(final PlanTargetResultRepository planTargetResultRepository,
            final PlanTargetRepository planTargetRepository) {
        this.planTargetResultRepository = planTargetResultRepository;
        this.planTargetRepository = planTargetRepository;
    }

    public List<PlanTargetResultDTO> findAll() {
        final List<PlanTargetResult> planTargetResults = planTargetResultRepository.findAll(Sort.by("id"));
        return planTargetResults.stream()
                .map(planTargetResult -> mapToDTO(planTargetResult, new PlanTargetResultDTO()))
                .toList();
    }

    public PlanTargetResultDTO get(final Integer id) {
        return planTargetResultRepository.findById(id)
                .map(planTargetResult -> mapToDTO(planTargetResult, new PlanTargetResultDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final PlanTargetResultDTO planTargetResultDTO) {
        final PlanTargetResult planTargetResult = new PlanTargetResult();
        mapToEntity(planTargetResultDTO, planTargetResult);
        return planTargetResultRepository.save(planTargetResult).getId();
    }

    public void update(final Integer id, final PlanTargetResultDTO planTargetResultDTO) {
        final PlanTargetResult planTargetResult = planTargetResultRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planTargetResultDTO, planTargetResult);
        planTargetResultRepository.save(planTargetResult);
    }

    public void delete(final Integer id) {
        planTargetResultRepository.deleteById(id);
    }

    private PlanTargetResultDTO mapToDTO(final PlanTargetResult planTargetResult,
            final PlanTargetResultDTO planTargetResultDTO) {
        planTargetResultDTO.setId(planTargetResult.getId());
        planTargetResultDTO.setResult(planTargetResult.getResult());
        planTargetResultDTO.setNote(planTargetResult.getNote());
        planTargetResultDTO.setCreatedAt(planTargetResult.getCreatedAt());
        planTargetResultDTO.setUpdatedAt(planTargetResult.getUpdatedAt());
        planTargetResultDTO.setCreatedBy(planTargetResult.getCreatedBy());
        planTargetResultDTO.setPlanTarget(planTargetResult.getPlanTarget() == null ? null : planTargetResult.getPlanTarget().getId());
        return planTargetResultDTO;
    }

    private PlanTargetResult mapToEntity(final PlanTargetResultDTO planTargetResultDTO,
            final PlanTargetResult planTargetResult) {
        planTargetResult.setResult(planTargetResultDTO.getResult());
        planTargetResult.setNote(planTargetResultDTO.getNote());
        planTargetResult.setCreatedAt(planTargetResultDTO.getCreatedAt());
        planTargetResult.setUpdatedAt(planTargetResultDTO.getUpdatedAt());
        planTargetResult.setCreatedBy(planTargetResultDTO.getCreatedBy());
        final PlanTarget planTarget = planTargetResultDTO.getPlanTarget() == null ? null : planTargetRepository.findById(planTargetResultDTO.getPlanTarget())
                .orElseThrow(() -> new NotFoundException("planTarget not found"));
        planTargetResult.setPlanTarget(planTarget);
        return planTargetResult;
    }

}
