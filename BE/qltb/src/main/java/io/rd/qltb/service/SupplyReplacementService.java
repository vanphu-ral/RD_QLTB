package io.rd.qltb.service;

import io.rd.qltb.domain.PlanResult;
import io.rd.qltb.domain.Supply;
import io.rd.qltb.domain.SupplyReplacement;
import io.rd.qltb.events.BeforeDeletePlanResult;
import io.rd.qltb.events.BeforeDeleteSupply;
import io.rd.qltb.model.SupplyReplacementDTO;
import io.rd.qltb.repos.PlanResultRepository;
import io.rd.qltb.repos.SupplyReplacementRepository;
import io.rd.qltb.repos.SupplyRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
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

    public SupplyReplacementDTO mapToDTO(final SupplyReplacement supplyReplacement,
                                          final SupplyReplacementDTO dto) {
        dto.setId(supplyReplacement.getId());
        dto.setQuantity(supplyReplacement.getQuantity());
        dto.setCode(supplyReplacement.getCode());
        dto.setName(supplyReplacement.getName());
        dto.setNote(supplyReplacement.getNote());
        dto.setCreatedAt(supplyReplacement.getCreatedAt());
        dto.setUpdatedAt(supplyReplacement.getUpdatedAt());
        dto.setCreatedBy(supplyReplacement.getCreatedBy());
        dto.setUpdatedBy(supplyReplacement.getUpdatedBy());

        // Sao chép Supply có kiểm soát
        if (supplyReplacement.getSupply() != null) {
            Supply supplyCopy = new Supply();
            supplyCopy.setId(supplyReplacement.getSupply().getId());
            supplyCopy.setCode(supplyReplacement.getSupply().getCode());
            supplyCopy.setName(supplyReplacement.getSupply().getName());
            supplyCopy.setDescription(supplyReplacement.getSupply().getDescription());
            supplyCopy.setSource(supplyReplacement.getSupply().getSource());
            supplyCopy.setCreatedAt(supplyReplacement.getSupply().getCreatedAt());
            supplyCopy.setUpdatedAt(supplyReplacement.getSupply().getUpdatedAt());
            supplyCopy.setCreatedBy(supplyReplacement.getSupply().getCreatedBy());
            supplyCopy.setUpdatedBy(supplyReplacement.getSupply().getUpdatedBy());
            supplyCopy.setStatus(supplyReplacement.getSupply().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            supplyCopy.setGroup(null);
            supplyCopy.setSupplySupplyDetails(null);
            supplyCopy.setSupplyDeviceSupplyUsages(null);
            supplyCopy.setSupplySupplyReplacements(null);
            supplyCopy.setOldSupplySupplyReplacementHistories(null);
            supplyCopy.setNewSupplySupplyReplacementHistories(null);

            dto.setSupply(supplyCopy);
        } else {
            dto.setSupply(null);
        }

        // Sao chép PlanResult có kiểm soát
        if (supplyReplacement.getPlanResult() != null) {
            PlanResult resultCopy = new PlanResult();
            resultCopy.setId(supplyReplacement.getPlanResult().getId());
            resultCopy.setCode(supplyReplacement.getPlanResult().getCode());
            resultCopy.setNote(supplyReplacement.getPlanResult().getNote());
            resultCopy.setStatus(supplyReplacement.getPlanResult().getStatus());
            resultCopy.setStatusRepair(supplyReplacement.getPlanResult().getStatusRepair());
            resultCopy.setCreatedAt(supplyReplacement.getPlanResult().getCreatedAt());
            resultCopy.setUpdatedAt(supplyReplacement.getPlanResult().getUpdatedAt());
            resultCopy.setCreatedBy(supplyReplacement.getPlanResult().getCreatedBy());
            resultCopy.setUpdatedBy(supplyReplacement.getPlanResult().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp
            resultCopy.setPlanResultPlanResultDetails(null);
            resultCopy.setPlanResultErrorReports(null);
            resultCopy.setPlanResultAcceptances(null);
            resultCopy.setPlanResultSupplyReplacements(null);

            dto.setPlanResult(resultCopy);
        } else {
            dto.setPlanResult(null);
        }

        return dto;
    }

    public SupplyReplacement mapToEntity(final SupplyReplacementDTO supplyReplacementDTO,
            final SupplyReplacement supplyReplacement) {
        supplyReplacement.setQuantity(supplyReplacementDTO.getQuantity());
        supplyReplacement.setCode(supplyReplacementDTO.getCode());
        supplyReplacement.setName(supplyReplacementDTO.getName());
        supplyReplacement.setNote(supplyReplacementDTO.getNote());
        supplyReplacement.setCreatedAt(supplyReplacementDTO.getCreatedAt());
        supplyReplacement.setUpdatedAt(supplyReplacementDTO.getUpdatedAt());
        supplyReplacement.setUpdatedBy(supplyReplacementDTO.getUpdatedBy());
        supplyReplacement.setCreatedBy(supplyReplacementDTO.getCreatedBy());
        final PlanResult planResult = supplyReplacementDTO.getPlanResult() == null ? null : planResultRepository.findById(supplyReplacementDTO.getPlanResult().getId())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        supplyReplacement.setPlanResult(planResult);
        final Supply supply = supplyReplacementDTO.getSupply() == null ? null : supplyRepository.findById(supplyReplacementDTO.getSupply().getId())
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
