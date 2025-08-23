package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.PlanType;
import rd.project.qltb.model.PlanTypeDTO;
import rd.project.qltb.repos.PlanRepository;
import rd.project.qltb.repos.PlanTypeRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class PlanTypeService {

    private final PlanTypeRepository planTypeRepository;
    private final PlanRepository planRepository;

    public PlanTypeService(final PlanTypeRepository planTypeRepository,
            final PlanRepository planRepository) {
        this.planTypeRepository = planTypeRepository;
        this.planRepository = planRepository;
    }

    public List<PlanTypeDTO> findAll() {
        final List<PlanType> planTypes = planTypeRepository.findAll(Sort.by("id"));
        return planTypes.stream()
                .map(planType -> mapToDTO(planType, new PlanTypeDTO()))
                .toList();
    }

    public PlanTypeDTO get(final Long id) {
        return planTypeRepository.findById(id)
                .map(planType -> mapToDTO(planType, new PlanTypeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanTypeDTO planTypeDTO) {
        final PlanType planType = new PlanType();
        mapToEntity(planTypeDTO, planType);
        return planTypeRepository.save(planType).getId();
    }

    public void update(final Long id, final PlanTypeDTO planTypeDTO) {
        final PlanType planType = planTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planTypeDTO, planType);
        planTypeRepository.save(planType);
    }

    public void delete(final Long id) {
        planTypeRepository.deleteById(id);
    }

    private PlanTypeDTO mapToDTO(final PlanType planType, final PlanTypeDTO planTypeDTO) {
        planTypeDTO.setId(planType.getId());
        planTypeDTO.setCode(planType.getCode());
        planTypeDTO.setName(planType.getName());
        planTypeDTO.setCreatedAt(planType.getCreatedAt());
        planTypeDTO.setUpdatedAt(planType.getUpdatedAt());
        planTypeDTO.setCreatedBy(planType.getCreatedBy());
        return planTypeDTO;
    }

    private PlanType mapToEntity(final PlanTypeDTO planTypeDTO, final PlanType planType) {
        planType.setCode(planTypeDTO.getCode());
        planType.setName(planTypeDTO.getName());
        planType.setCreatedAt(planTypeDTO.getCreatedAt());
        planType.setUpdatedAt(planTypeDTO.getUpdatedAt());
        planType.setCreatedBy(planTypeDTO.getCreatedBy());
        return planType;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final PlanType planType = planTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Plan planTypePlan = planRepository.findFirstByPlanType(planType);
        if (planTypePlan != null) {
            referencedWarning.setKey("planType.plan.planType.referenced");
            referencedWarning.addParam(planTypePlan.getId());
            return referencedWarning;
        }
        return null;
    }

}
