package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.PlanResult;
import rd.project.qltb.domain.Supply;
import rd.project.qltb.domain.SupplyReplacement;
import rd.project.qltb.model.SupplyReplacementDTO;
import rd.project.qltb.repos.PlanResultRepository;
import rd.project.qltb.repos.SupplyReplacementRepository;
import rd.project.qltb.repos.SupplyRepository;
import rd.project.qltb.util.NotFoundException;


@Service
public class SupplyReplacementService {

    private final SupplyReplacementRepository supplyReplacementRepository;
    private final PlanResultRepository planResultRepository;
    private final SupplyRepository supplyRepository;

    public SupplyReplacementService(final SupplyReplacementRepository supplyReplacementRepository,
            final PlanResultRepository planResultRepository,
            final SupplyRepository supplyRepository) {
        this.supplyReplacementRepository = supplyReplacementRepository;
        this.planResultRepository = planResultRepository;
        this.supplyRepository = supplyRepository;
    }

    public List<SupplyReplacementDTO> findAll() {
        final List<SupplyReplacement> supplyReplacements = supplyReplacementRepository.findAll(Sort.by("id"));
        return supplyReplacements.stream()
                .map(supplyReplacement -> mapToDTO(supplyReplacement, new SupplyReplacementDTO()))
                .toList();
    }

    public SupplyReplacementDTO get(final Long id) {
        return supplyReplacementRepository.findById(id)
                .map(supplyReplacement -> mapToDTO(supplyReplacement, new SupplyReplacementDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SupplyReplacementDTO supplyReplacementDTO) {
        final SupplyReplacement supplyReplacement = new SupplyReplacement();
        mapToEntity(supplyReplacementDTO, supplyReplacement);
        return supplyReplacementRepository.save(supplyReplacement).getId();
    }

    public void update(final Long id, final SupplyReplacementDTO supplyReplacementDTO) {
        final SupplyReplacement supplyReplacement = supplyReplacementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyReplacementDTO, supplyReplacement);
        supplyReplacementRepository.save(supplyReplacement);
    }

    public void delete(final Long id) {
        supplyReplacementRepository.deleteById(id);
    }

    private SupplyReplacementDTO mapToDTO(final SupplyReplacement supplyReplacement,
            final SupplyReplacementDTO supplyReplacementDTO) {
        supplyReplacementDTO.setId(supplyReplacement.getId());
        supplyReplacementDTO.setQuantity(supplyReplacement.getQuantity());
        supplyReplacementDTO.setSapCode(supplyReplacement.getSapCode());
        supplyReplacementDTO.setSapName(supplyReplacement.getSapName());
        supplyReplacementDTO.setNote(supplyReplacement.getNote());
        supplyReplacementDTO.setCreatedAt(supplyReplacement.getCreatedAt());
        supplyReplacementDTO.setUpdatedAt(supplyReplacement.getUpdatedAt());
        supplyReplacementDTO.setCreatedBy(supplyReplacement.getCreatedBy());
        supplyReplacementDTO.setPlanResult(supplyReplacement.getPlanResult() == null ? null : supplyReplacement.getPlanResult().getId());
        supplyReplacementDTO.setSupply(supplyReplacement.getSupply() == null ? null : supplyReplacement.getSupply().getId());
        return supplyReplacementDTO;
    }

    private SupplyReplacement mapToEntity(final SupplyReplacementDTO supplyReplacementDTO,
            final SupplyReplacement supplyReplacement) {
        supplyReplacement.setQuantity(supplyReplacementDTO.getQuantity());
        supplyReplacement.setSapCode(supplyReplacementDTO.getSapCode());
        supplyReplacement.setSapName(supplyReplacementDTO.getSapName());
        supplyReplacement.setNote(supplyReplacementDTO.getNote());
        supplyReplacement.setCreatedAt(supplyReplacementDTO.getCreatedAt());
        supplyReplacement.setUpdatedAt(supplyReplacementDTO.getUpdatedAt());
        supplyReplacement.setCreatedBy(supplyReplacementDTO.getCreatedBy());
        final PlanResult planResult = supplyReplacementDTO.getPlanResult() == null ? null : planResultRepository.findById(supplyReplacementDTO.getPlanResult())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        supplyReplacement.setPlanResult(planResult);
        final Supply supply = supplyReplacementDTO.getSupply() == null ? null : supplyRepository.findById(supplyReplacementDTO.getSupply())
                .orElseThrow(() -> new NotFoundException("supply not found"));
        supplyReplacement.setSupply(supply);
        return supplyReplacement;
    }

}
