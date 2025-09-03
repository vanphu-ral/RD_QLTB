package io.qltb.qltb.service;

import io.qltb.qltb.domain.PlanResult;
import io.qltb.qltb.domain.Supply;
import io.qltb.qltb.domain.SupplyReplacement;
import io.qltb.qltb.events.BeforeDeletePlanResult;
import io.qltb.qltb.events.BeforeDeleteSupply;
import io.qltb.qltb.model.SupplyReplacementDTO;
import io.qltb.qltb.repos.PlanResultRepository;
import io.qltb.qltb.repos.SupplyReplacementRepository;
import io.qltb.qltb.repos.SupplyRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


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
        final SupplyReplacement supplyReplacement = supplyReplacementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        supplyReplacementRepository.delete(supplyReplacement);
    }

    private SupplyReplacementDTO mapToDTO(final SupplyReplacement supplyReplacement,
            final SupplyReplacementDTO supplyReplacementDTO) {
        supplyReplacementDTO.setId(supplyReplacement.getId());
        supplyReplacementDTO.setQuantity(supplyReplacement.getQuantity());
        supplyReplacementDTO.setCode(supplyReplacement.getCode());
        supplyReplacementDTO.setName(supplyReplacement.getName());
        supplyReplacementDTO.setNote(supplyReplacement.getNote());
        supplyReplacementDTO.setCreatedAt(supplyReplacement.getCreatedAt());
        supplyReplacementDTO.setUpdatedAt(supplyReplacement.getUpdatedAt());
        supplyReplacementDTO.setUpdatedBy(supplyReplacement.getUpdatedBy());
        supplyReplacementDTO.setCreatedBy(supplyReplacement.getCreatedBy());
        supplyReplacementDTO.setPlanResult(supplyReplacement.getPlanResult() == null ? null : supplyReplacement.getPlanResult().getId());
        supplyReplacementDTO.setSupply(supplyReplacement.getSupply() == null ? null : supplyReplacement.getSupply().getId());
        return supplyReplacementDTO;
    }

    private SupplyReplacement mapToEntity(final SupplyReplacementDTO supplyReplacementDTO,
            final SupplyReplacement supplyReplacement) {
        supplyReplacement.setQuantity(supplyReplacementDTO.getQuantity());
        supplyReplacement.setCode(supplyReplacementDTO.getCode());
        supplyReplacement.setName(supplyReplacementDTO.getName());
        supplyReplacement.setNote(supplyReplacementDTO.getNote());
        supplyReplacement.setCreatedAt(supplyReplacementDTO.getCreatedAt());
        supplyReplacement.setUpdatedAt(supplyReplacementDTO.getUpdatedAt());
        supplyReplacement.setUpdatedBy(supplyReplacementDTO.getUpdatedBy());
        supplyReplacement.setCreatedBy(supplyReplacementDTO.getCreatedBy());
        final PlanResult planResult = supplyReplacementDTO.getPlanResult() == null ? null : planResultRepository.findById(supplyReplacementDTO.getPlanResult())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        supplyReplacement.setPlanResult(planResult);
        final Supply supply = supplyReplacementDTO.getSupply() == null ? null : supplyRepository.findById(supplyReplacementDTO.getSupply())
                .orElseThrow(() -> new NotFoundException("supply not found"));
        supplyReplacement.setSupply(supply);
        return supplyReplacement;
    }

    @EventListener(BeforeDeletePlanResult.class)
    public void on(final BeforeDeletePlanResult event) {
        final ReferencedException referencedException = new ReferencedException();
        final SupplyReplacement planResultSupplyReplacement = supplyReplacementRepository.findFirstByPlanResultId(event.getId());
        if (planResultSupplyReplacement != null) {
            referencedException.setKey("planResult.supplyReplacement.planResult.referenced");
            referencedException.addParam(planResultSupplyReplacement.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteSupply.class)
    public void on(final BeforeDeleteSupply event) {
        final ReferencedException referencedException = new ReferencedException();
        final SupplyReplacement supplySupplyReplacement = supplyReplacementRepository.findFirstBySupplyId(event.getId());
        if (supplySupplyReplacement != null) {
            referencedException.setKey("supply.supplyReplacement.supply.referenced");
            referencedException.addParam(supplySupplyReplacement.getId());
            throw referencedException;
        }
    }

}
