package io.rd.qltb.service;

import io.rd.qltb.domain.Supply;
import io.rd.qltb.domain.SupplyReplacementHistory;
import io.rd.qltb.events.BeforeDeleteSupply;
import io.rd.qltb.model.SupplyReplacementHistoryDTO;
import io.rd.qltb.repos.SupplyReplacementHistoryRepository;
import io.rd.qltb.repos.SupplyRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.util.Collections;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SupplyReplacementHistoryService {

    private final SupplyReplacementHistoryRepository supplyReplacementHistoryRepository;
    private final SupplyRepository supplyRepository;

    public SupplyReplacementHistoryService(
            final SupplyReplacementHistoryRepository supplyReplacementHistoryRepository,
            final SupplyRepository supplyRepository) {
        this.supplyReplacementHistoryRepository = supplyReplacementHistoryRepository;
        this.supplyRepository = supplyRepository;
    }

    public List<SupplyReplacementHistoryDTO> findAll() {
        final List<SupplyReplacementHistory> supplyReplacementHistories = supplyReplacementHistoryRepository.findAll(Sort.by("id"));
        return supplyReplacementHistories.stream()
                .map(supplyReplacementHistory -> mapToDTO(supplyReplacementHistory, new SupplyReplacementHistoryDTO()))
                .toList();
    }

    public SupplyReplacementHistoryDTO get(final Long id) {
        return supplyReplacementHistoryRepository.findById(id)
                .map(supplyReplacementHistory -> mapToDTO(supplyReplacementHistory, new SupplyReplacementHistoryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SupplyReplacementHistoryDTO supplyReplacementHistoryDTO) {
        final SupplyReplacementHistory supplyReplacementHistory = new SupplyReplacementHistory();
        mapToEntity(supplyReplacementHistoryDTO, supplyReplacementHistory);
        return supplyReplacementHistoryRepository.save(supplyReplacementHistory).getId();
    }

    public List<Long> createList(List<SupplyReplacementHistoryDTO> dtos) {
        return dtos.stream()
                .map(dto -> {
                    SupplyReplacementHistory entity = new SupplyReplacementHistory();
                    mapToEntity(dto, entity);
                    return supplyReplacementHistoryRepository.save(entity).getId();
                })
                .toList();
    }

    public void update(final Long id,
            final SupplyReplacementHistoryDTO supplyReplacementHistoryDTO) {
        final SupplyReplacementHistory supplyReplacementHistory = supplyReplacementHistoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyReplacementHistoryDTO, supplyReplacementHistory);
        supplyReplacementHistoryRepository.save(supplyReplacementHistory);
    }

    public void delete(final Long id) {
        final SupplyReplacementHistory supplyReplacementHistory = supplyReplacementHistoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        supplyReplacementHistoryRepository.delete(supplyReplacementHistory);
    }

    public List<SupplyReplacementHistoryDTO> getByPlanResultId(final Long planResultId) {
        if (planResultId == null) {
            return Collections.emptyList();
        }
        final List<SupplyReplacementHistory> histories = supplyReplacementHistoryRepository.findByPlanResultIdOrderByCreatedAtAsc(planResultId);
        return histories.stream()
                .map(h -> mapToDTO(h, new SupplyReplacementHistoryDTO()))
                .toList();
    }

    private SupplyReplacementHistoryDTO mapToDTO(
            final SupplyReplacementHistory supplyReplacementHistory,
            final SupplyReplacementHistoryDTO dto) {

        dto.setId(supplyReplacementHistory.getId());
        dto.setQuantityOld(supplyReplacementHistory.getQuantityOld());
        dto.setQuantityChange(supplyReplacementHistory.getQuantityChange());
        dto.setReason(supplyReplacementHistory.getReason());
        dto.setPlanId(supplyReplacementHistory.getPlanId());
        dto.setPlanResultId(supplyReplacementHistory.getPlanResultId());
        dto.setCreatedAt(supplyReplacementHistory.getCreatedAt());
        dto.setCreatedBy(supplyReplacementHistory.getCreatedBy());

        // Sao chép oldSupply có kiểm soát
        if (supplyReplacementHistory.getOldSupply() != null) {
            Supply oldSupplyCopy = new Supply();
            oldSupplyCopy.setId(supplyReplacementHistory.getOldSupply().getId());
            oldSupplyCopy.setCode(supplyReplacementHistory.getOldSupply().getCode());
            oldSupplyCopy.setName(supplyReplacementHistory.getOldSupply().getName());
            oldSupplyCopy.setDescription(supplyReplacementHistory.getOldSupply().getDescription());
            oldSupplyCopy.setSource(supplyReplacementHistory.getOldSupply().getSource());
            oldSupplyCopy.setCreatedAt(supplyReplacementHistory.getOldSupply().getCreatedAt());
            oldSupplyCopy.setUpdatedAt(supplyReplacementHistory.getOldSupply().getUpdatedAt());
            oldSupplyCopy.setCreatedBy(supplyReplacementHistory.getOldSupply().getCreatedBy());
            oldSupplyCopy.setUpdatedBy(supplyReplacementHistory.getOldSupply().getUpdatedBy());
            oldSupplyCopy.setStatus(supplyReplacementHistory.getOldSupply().getStatus());

            // Xóa các quan hệ con
            oldSupplyCopy.setGroup(null);
            oldSupplyCopy.setSupplySupplyDetails(null);
            oldSupplyCopy.setSupplyDeviceSupplyUsages(null);
            oldSupplyCopy.setSupplySupplyReplacements(null);
            oldSupplyCopy.setOldSupplySupplyReplacementHistories(null);
            oldSupplyCopy.setNewSupplySupplyReplacementHistories(null);

            dto.setOldSupply(oldSupplyCopy);
        } else {
            dto.setOldSupply(null);
        }

        // Sao chép newSupply có kiểm soát
        if (supplyReplacementHistory.getNewSupply() != null) {
            Supply newSupplyCopy = new Supply();
            newSupplyCopy.setId(supplyReplacementHistory.getNewSupply().getId());
            newSupplyCopy.setCode(supplyReplacementHistory.getNewSupply().getCode());
            newSupplyCopy.setName(supplyReplacementHistory.getNewSupply().getName());
            newSupplyCopy.setDescription(supplyReplacementHistory.getNewSupply().getDescription());
            newSupplyCopy.setSource(supplyReplacementHistory.getNewSupply().getSource());
            newSupplyCopy.setCreatedAt(supplyReplacementHistory.getNewSupply().getCreatedAt());
            newSupplyCopy.setUpdatedAt(supplyReplacementHistory.getNewSupply().getUpdatedAt());
            newSupplyCopy.setCreatedBy(supplyReplacementHistory.getNewSupply().getCreatedBy());
            newSupplyCopy.setUpdatedBy(supplyReplacementHistory.getNewSupply().getUpdatedBy());
            newSupplyCopy.setStatus(supplyReplacementHistory.getNewSupply().getStatus());

            // Xóa các quan hệ con
            newSupplyCopy.setGroup(null);
            newSupplyCopy.setSupplySupplyDetails(null);
            newSupplyCopy.setSupplyDeviceSupplyUsages(null);
            newSupplyCopy.setSupplySupplyReplacements(null);
            newSupplyCopy.setOldSupplySupplyReplacementHistories(null);
            newSupplyCopy.setNewSupplySupplyReplacementHistories(null);

            dto.setNewSupply(newSupplyCopy);
        } else {
            dto.setNewSupply(null);
        }

        return dto;
    }


    private SupplyReplacementHistory mapToEntity(
            final SupplyReplacementHistoryDTO supplyReplacementHistoryDTO,
            final SupplyReplacementHistory supplyReplacementHistory) {
        supplyReplacementHistory.setQuantityOld(supplyReplacementHistoryDTO.getQuantityOld());
        supplyReplacementHistory.setQuantityChange(supplyReplacementHistoryDTO.getQuantityChange());
        supplyReplacementHistory.setReason(supplyReplacementHistoryDTO.getReason());
        supplyReplacementHistory.setPlanId(supplyReplacementHistoryDTO.getPlanId());
        supplyReplacementHistory.setPlanResultId(supplyReplacementHistoryDTO.getPlanResultId());
        supplyReplacementHistory.setCreatedAt(supplyReplacementHistoryDTO.getCreatedAt());
        supplyReplacementHistory.setCreatedBy(supplyReplacementHistoryDTO.getCreatedBy());
        final Supply oldSupply = supplyReplacementHistoryDTO.getOldSupply() == null ? null : supplyRepository.findById(supplyReplacementHistoryDTO.getOldSupply().getId())
                .orElseThrow(() -> new NotFoundException("oldSupply not found"));
        supplyReplacementHistory.setOldSupply(oldSupply);
        final Supply newSupply = supplyReplacementHistoryDTO.getNewSupply() == null ? null : supplyRepository.findById(supplyReplacementHistoryDTO.getNewSupply().getId())
                .orElseThrow(() -> new NotFoundException("newSupply not found"));
        supplyReplacementHistory.setNewSupply(newSupply);
        return supplyReplacementHistory;
    }

    @EventListener(BeforeDeleteSupply.class)
    public void on(final BeforeDeleteSupply event) {
        final ReferencedException referencedException = new ReferencedException();
        final SupplyReplacementHistory oldSupplySupplyReplacementHistory = supplyReplacementHistoryRepository.findFirstByOldSupplyId(event.getId());
        if (oldSupplySupplyReplacementHistory != null) {
            referencedException.setKey("supply.supplyReplacementHistory.oldSupply.referenced");
            referencedException.addParam(oldSupplySupplyReplacementHistory.getId());
            throw referencedException;
        }
        final SupplyReplacementHistory newSupplySupplyReplacementHistory = supplyReplacementHistoryRepository.findFirstByNewSupplyId(event.getId());
        if (newSupplySupplyReplacementHistory != null) {
            referencedException.setKey("supply.supplyReplacementHistory.newSupply.referenced");
            referencedException.addParam(newSupplySupplyReplacementHistory.getId());
            throw referencedException;
        }
    }

}
