package io.rd.qltb.service;

import io.rd.qltb.domain.PlanResult;
import io.rd.qltb.domain.Supply;
import io.rd.qltb.domain.SupplyDetail;
import io.rd.qltb.domain.SupplyReplacement;
import io.rd.qltb.events.BeforeDeletePlanResult;
import io.rd.qltb.events.BeforeDeleteSupply;
import io.rd.qltb.model.SupplyReplacementDTO;
import io.rd.qltb.repos.PlanResultRepository;
import io.rd.qltb.repos.SupplyDetailRepository;
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
    private final SupplyDetailRepository supplyDetailRepository;

    public SupplyReplacementService(final SupplyReplacementRepository supplyReplacementRepository,
                                    final PlanResultRepository planResultRepository,
                                    final SupplyRepository supplyRepository, SupplyDetailRepository supplyDetailRepository) {
        this.supplyReplacementRepository = supplyReplacementRepository;
        this.planResultRepository = planResultRepository;
        this.supplyRepository = supplyRepository;
        this.supplyDetailRepository = supplyDetailRepository;
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
            SupplyDetail supplyCopy = new SupplyDetail();
            supplyCopy.setId(supplyReplacement.getSupply().getId());
            supplyCopy.setSerial(supplyReplacement.getSupply().getSerial());
            supplyCopy.setImportDate(supplyReplacement.getSupply().getImportDate());
            supplyCopy.setSupplier(supplyReplacement.getSupply().getSupplier());
            supplyCopy.setPrice(supplyReplacement.getSupply().getPrice());
            supplyCopy.setUnit(supplyReplacement.getSupply().getUnit());
            supplyCopy.setCurrency(supplyReplacement.getSupply().getCurrency());
            supplyCopy.setQuantity(supplyReplacement.getSupply().getQuantity());
            supplyCopy.setStatus(supplyReplacement.getSupply().getStatus());
            supplyCopy.setStatus(supplyReplacement.getSupply().getStatus());

             supplyCopy.getSupply().setGroup(null);
             supplyCopy.getSupply().setSupplySupplyDetails(null);
             supplyCopy.getSupply().setSupplyDeviceSupplyUsages(null);


            dto.setSupply(supplyCopy);
        } else {
            dto.setSupply(null);
        }

        // Sao chép PlanResult có kiểm soát
        if (supplyReplacement.getPlanResult() != null) {
            PlanResult resultCopy = new PlanResult();
            resultCopy.setId(supplyReplacement.getPlanResult().getId());
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
        final SupplyDetail supply = supplyReplacementDTO.getSupply() == null ? null : supplyDetailRepository.findById(supplyReplacementDTO.getSupply().getId())
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
