package io.rd.qltb.service;

import io.rd.qltb.domain.SupplyDetail;
import io.rd.qltb.domain.SupplyReplacementHistory;
import io.rd.qltb.events.BeforeDeleteSupply;
import io.rd.qltb.model.SupplyReplacementHistoryDTO;
import io.rd.qltb.repos.SupplyDetailRepository;
import io.rd.qltb.repos.SupplyReplacementHistoryRepository;
import io.rd.qltb.repos.SupplyReplacementRepository;
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
    private final SupplyReplacementRepository supplyReplacementRepository;
    private final SupplyDetailRepository supplyDetailRepository;

    public SupplyReplacementHistoryService(
            final SupplyReplacementHistoryRepository supplyReplacementHistoryRepository,
            final SupplyRepository supplyRepository, SupplyReplacementRepository supplyReplacementRepository, SupplyDetailRepository supplyDetailRepository) {
        this.supplyReplacementHistoryRepository = supplyReplacementHistoryRepository;
        this.supplyRepository = supplyRepository;
        this.supplyReplacementRepository = supplyReplacementRepository;
        this.supplyDetailRepository = supplyDetailRepository;
    }

    public List<SupplyReplacementHistoryDTO> findAll() {
        final List<SupplyReplacementHistory> supplyReplacementHistories = supplyReplacementHistoryRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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

    public SupplyReplacementHistoryDTO mapToDTO(
            final SupplyReplacementHistory supplyReplacementHistory,
            final SupplyReplacementHistoryDTO dto) {

        dto.setId(supplyReplacementHistory.getId());
        dto.setQuantityOld(supplyReplacementHistory.getQuantityOld());
        dto.setQuantityChange(supplyReplacementHistory.getQuantityChange());
        dto.setReason(supplyReplacementHistory.getReason());
        dto.setPlanId(supplyReplacementHistory.getPlanId());
        dto.setDeviceId(supplyReplacementHistory.getDeviceId());
        dto.setPlanResultId(supplyReplacementHistory.getPlanResultId());
        dto.setCreatedAt(supplyReplacementHistory.getCreatedAt());
        dto.setCreatedBy(supplyReplacementHistory.getCreatedBy());

        // Sao chép oldSupply có kiểm soát
        if (supplyReplacementHistory.getOldSupplyDetail() != null) {
            SupplyDetail oldSupplyCopy = new SupplyDetail();
            oldSupplyCopy.setId(supplyReplacementHistory.getOldSupplyDetail().getId());
            oldSupplyCopy.setSerial(supplyReplacementHistory.getOldSupplyDetail().getSerial());
            oldSupplyCopy.setImportDate(supplyReplacementHistory.getOldSupplyDetail().getImportDate());
            oldSupplyCopy.setSupplier(supplyReplacementHistory.getOldSupplyDetail().getSupplier());
            oldSupplyCopy.setUnit(supplyReplacementHistory.getOldSupplyDetail().getUnit());
            oldSupplyCopy.setPrice(supplyReplacementHistory.getOldSupplyDetail().getPrice());
            oldSupplyCopy.setCurrency(supplyReplacementHistory.getOldSupplyDetail().getCurrency());
            oldSupplyCopy.setQuantity(supplyReplacementHistory.getOldSupplyDetail().getQuantity());
            oldSupplyCopy.setStatus(supplyReplacementHistory.getOldSupplyDetail().getStatus());
            oldSupplyCopy.setSupply(supplyReplacementHistory.getOldSupplyDetail().getSupply());
            // Xóa các quan hệ con
            oldSupplyCopy.getSupply().setGroup(null);
            oldSupplyCopy.getSupply().setSupplySupplyDetails(null);



            dto.setOldSupplyDetail(oldSupplyCopy);
        } else {
            dto.setOldSupplyDetail(null);
        }

        // Sao chép newSupply có kiểm soát
        if (supplyReplacementHistory.getNewSupplyDetail() != null) {
            SupplyDetail newSupplyCopy = new SupplyDetail();
            newSupplyCopy.setId(supplyReplacementHistory.getNewSupplyDetail().getId());
            newSupplyCopy.setId(supplyReplacementHistory.getOldSupplyDetail().getId());
            newSupplyCopy.setSerial(supplyReplacementHistory.getOldSupplyDetail().getSerial());
            newSupplyCopy.setImportDate(supplyReplacementHistory.getOldSupplyDetail().getImportDate());
            newSupplyCopy.setSupplier(supplyReplacementHistory.getOldSupplyDetail().getSupplier());
            newSupplyCopy.setUnit(supplyReplacementHistory.getOldSupplyDetail().getUnit());
            newSupplyCopy.setPrice(supplyReplacementHistory.getOldSupplyDetail().getPrice());
            newSupplyCopy.setCurrency(supplyReplacementHistory.getOldSupplyDetail().getCurrency());
            newSupplyCopy.setQuantity(supplyReplacementHistory.getOldSupplyDetail().getQuantity());
            newSupplyCopy.setStatus(supplyReplacementHistory.getOldSupplyDetail().getStatus());
            newSupplyCopy.setSupply(supplyReplacementHistory.getNewSupplyDetail().getSupply());
            // Xóa các quan hệ con
            newSupplyCopy.getSupply().setGroup(null);
            newSupplyCopy.getSupply().setSupplySupplyDetails(null);


            dto.setNewSupplyDetail(newSupplyCopy);
        } else {
            dto.setNewSupplyDetail(null);
        }

        return dto;
    }


    public SupplyReplacementHistory mapToEntity(
            final SupplyReplacementHistoryDTO supplyReplacementHistoryDTO,
            final SupplyReplacementHistory supplyReplacementHistory) {
        supplyReplacementHistory.setQuantityOld(supplyReplacementHistoryDTO.getQuantityOld());
        supplyReplacementHistory.setQuantityChange(supplyReplacementHistoryDTO.getQuantityChange());
        supplyReplacementHistory.setReason(supplyReplacementHistoryDTO.getReason());
        supplyReplacementHistory.setPlanId(supplyReplacementHistoryDTO.getPlanId());
        supplyReplacementHistory.setDeviceId(supplyReplacementHistoryDTO.getDeviceId());
        supplyReplacementHistory.setPlanResultId(supplyReplacementHistoryDTO.getPlanResultId());
        supplyReplacementHistory.setCreatedAt(supplyReplacementHistoryDTO.getCreatedAt());
        supplyReplacementHistory.setCreatedBy(supplyReplacementHistoryDTO.getCreatedBy());
        final SupplyDetail oldSupply = supplyReplacementHistoryDTO.getOldSupplyDetail() == null ? null : supplyDetailRepository.findById(supplyReplacementHistoryDTO.getOldSupplyDetail().getId())
                .orElseThrow(() -> new NotFoundException("oldSupply not found"));
        supplyReplacementHistory.setOldSupplyDetail(oldSupply);
        final SupplyDetail newSupply = supplyReplacementHistoryDTO.getNewSupplyDetail() == null ? null : supplyDetailRepository.findById(supplyReplacementHistoryDTO.getNewSupplyDetail().getId())
                .orElseThrow(() -> new NotFoundException("newSupply not found"));
        supplyReplacementHistory.setNewSupplyDetail(newSupply);
        return supplyReplacementHistory;
    }

    @EventListener(BeforeDeleteSupply.class)
    public void on(final BeforeDeleteSupply event) {
        final ReferencedException referencedException = new ReferencedException();
        final SupplyReplacementHistory oldSupplySupplyReplacementHistory = supplyReplacementHistoryRepository.findFirstByOldSupplyDetailId(event.getId());
        if (oldSupplySupplyReplacementHistory != null) {
            referencedException.setKey("supply.supplyReplacementHistory.oldSupply.referenced");
            referencedException.addParam(oldSupplySupplyReplacementHistory.getId());
            throw referencedException;
        }
        final SupplyReplacementHistory newSupplySupplyReplacementHistory = supplyReplacementHistoryRepository.findFirstByNewSupplyDetailId(event.getId());
        if (newSupplySupplyReplacementHistory != null) {
            referencedException.setKey("supply.supplyReplacementHistory.newSupply.referenced");
            referencedException.addParam(newSupplySupplyReplacementHistory.getId());
            throw referencedException;
        }
    }

}
