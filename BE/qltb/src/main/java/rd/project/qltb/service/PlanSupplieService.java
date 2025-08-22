package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.PlanSupplie;
import rd.project.qltb.model.PlanSupplieDTO;
import rd.project.qltb.repos.PlanRepository;
import rd.project.qltb.repos.PlanSupplieRepository;
import rd.project.qltb.util.NotFoundException;


@Service
public class PlanSupplieService {

    private final PlanSupplieRepository planSupplieRepository;
    private final PlanRepository planRepository;

    public PlanSupplieService(final PlanSupplieRepository planSupplieRepository,
            final PlanRepository planRepository) {
        this.planSupplieRepository = planSupplieRepository;
        this.planRepository = planRepository;
    }

    public List<PlanSupplieDTO> findAll() {
        final List<PlanSupplie> planSupplies = planSupplieRepository.findAll(Sort.by("id"));
        return planSupplies.stream()
                .map(planSupplie -> mapToDTO(planSupplie, new PlanSupplieDTO()))
                .toList();
    }

    public PlanSupplieDTO get(final Long id) {
        return planSupplieRepository.findById(id)
                .map(planSupplie -> mapToDTO(planSupplie, new PlanSupplieDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanSupplieDTO planSupplieDTO) {
        final PlanSupplie planSupplie = new PlanSupplie();
        mapToEntity(planSupplieDTO, planSupplie);
        return planSupplieRepository.save(planSupplie).getId();
    }

    public void update(final Long id, final PlanSupplieDTO planSupplieDTO) {
        final PlanSupplie planSupplie = planSupplieRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planSupplieDTO, planSupplie);
        planSupplieRepository.save(planSupplie);
    }

    public void delete(final Long id) {
        planSupplieRepository.deleteById(id);
    }

    private PlanSupplieDTO mapToDTO(final PlanSupplie planSupplie,
            final PlanSupplieDTO planSupplieDTO) {
        planSupplieDTO.setId(planSupplie.getId());
        planSupplieDTO.setSapCode(planSupplie.getSapCode());
        planSupplieDTO.setSapName(planSupplie.getSapName());
        planSupplieDTO.setDescription(planSupplie.getDescription());
        planSupplieDTO.setQuantity(planSupplie.getQuantity());
        planSupplieDTO.setPrice(planSupplie.getPrice());
        planSupplieDTO.setActiveValue(planSupplie.getActiveValue());
        planSupplieDTO.setFileScan(planSupplie.getFileScan());
        planSupplieDTO.setCreatedAt(planSupplie.getCreatedAt());
        planSupplieDTO.setUpdatedAt(planSupplie.getUpdatedAt());
        planSupplieDTO.setCreatedBy(planSupplie.getCreatedBy());
        planSupplieDTO.setPlan(planSupplie.getPlan() == null ? null : planSupplie.getPlan().getId());
        return planSupplieDTO;
    }

    private PlanSupplie mapToEntity(final PlanSupplieDTO planSupplieDTO,
            final PlanSupplie planSupplie) {
        planSupplie.setSapCode(planSupplieDTO.getSapCode());
        planSupplie.setSapName(planSupplieDTO.getSapName());
        planSupplie.setDescription(planSupplieDTO.getDescription());
        planSupplie.setQuantity(planSupplieDTO.getQuantity());
        planSupplie.setPrice(planSupplieDTO.getPrice());
        planSupplie.setActiveValue(planSupplieDTO.getActiveValue());
        planSupplie.setFileScan(planSupplieDTO.getFileScan());
        planSupplie.setCreatedAt(planSupplieDTO.getCreatedAt());
        planSupplie.setUpdatedAt(planSupplieDTO.getUpdatedAt());
        planSupplie.setCreatedBy(planSupplieDTO.getCreatedBy());
        final Plan plan = planSupplieDTO.getPlan() == null ? null : planRepository.findById(planSupplieDTO.getPlan())
                .orElseThrow(() -> new NotFoundException("plan not found"));
        planSupplie.setPlan(plan);
        return planSupplie;
    }

}
