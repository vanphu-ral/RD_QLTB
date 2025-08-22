package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.PlanTarget;
import rd.project.qltb.domain.PlanTargetResult;
import rd.project.qltb.model.PlanTargetDTO;
import rd.project.qltb.repos.BranchRepository;
import rd.project.qltb.repos.PlanTargetRepository;
import rd.project.qltb.repos.PlanTargetResultRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class PlanTargetService {

    private final PlanTargetRepository planTargetRepository;
    private final BranchRepository branchRepository;
    private final PlanTargetResultRepository planTargetResultRepository;

    public PlanTargetService(final PlanTargetRepository planTargetRepository,
            final BranchRepository branchRepository,
            final PlanTargetResultRepository planTargetResultRepository) {
        this.planTargetRepository = planTargetRepository;
        this.branchRepository = branchRepository;
        this.planTargetResultRepository = planTargetResultRepository;
    }

    public List<PlanTargetDTO> findAll() {
        final List<PlanTarget> planTargets = planTargetRepository.findAll(Sort.by("id"));
        return planTargets.stream()
                .map(planTarget -> mapToDTO(planTarget, new PlanTargetDTO()))
                .toList();
    }

    public PlanTargetDTO get(final Long id) {
        return planTargetRepository.findById(id)
                .map(planTarget -> mapToDTO(planTarget, new PlanTargetDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanTargetDTO planTargetDTO) {
        final PlanTarget planTarget = new PlanTarget();
        mapToEntity(planTargetDTO, planTarget);
        return planTargetRepository.save(planTarget).getId();
    }

    public void update(final Long id, final PlanTargetDTO planTargetDTO) {
        final PlanTarget planTarget = planTargetRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planTargetDTO, planTarget);
        planTargetRepository.save(planTarget);
    }

    public void delete(final Long id) {
        planTargetRepository.deleteById(id);
    }

    private PlanTargetDTO mapToDTO(final PlanTarget planTarget, final PlanTargetDTO planTargetDTO) {
        planTargetDTO.setId(planTarget.getId());
        planTargetDTO.setTargetDescription(planTarget.getTargetDescription());
        planTargetDTO.setTargetValue(planTarget.getTargetValue());
        planTargetDTO.setCritical(planTarget.getCritical());
        planTargetDTO.setCreatedAt(planTarget.getCreatedAt());
        planTargetDTO.setUpdatedAt(planTarget.getUpdatedAt());
        planTargetDTO.setCreatedBy(planTarget.getCreatedBy());
        planTargetDTO.setBranch(planTarget.getBranch() == null ? null : planTarget.getBranch().getId());
        return planTargetDTO;
    }

    private PlanTarget mapToEntity(final PlanTargetDTO planTargetDTO, final PlanTarget planTarget) {
        planTarget.setTargetDescription(planTargetDTO.getTargetDescription());
        planTarget.setTargetValue(planTargetDTO.getTargetValue());
        planTarget.setCritical(planTargetDTO.getCritical());
        planTarget.setCreatedAt(planTargetDTO.getCreatedAt());
        planTarget.setUpdatedAt(planTargetDTO.getUpdatedAt());
        planTarget.setCreatedBy(planTargetDTO.getCreatedBy());
        final Branch branch = planTargetDTO.getBranch() == null ? null : branchRepository.findById(planTargetDTO.getBranch())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        planTarget.setBranch(branch);
        return planTarget;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final PlanTarget planTarget = planTargetRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanTargetResult planTargetPlanTargetResult = planTargetResultRepository.findFirstByPlanTarget(planTarget);
        if (planTargetPlanTargetResult != null) {
            referencedWarning.setKey("planTarget.planTargetResult.planTarget.referenced");
            referencedWarning.addParam(planTargetPlanTargetResult.getId());
            return referencedWarning;
        }
        return null;
    }

}
