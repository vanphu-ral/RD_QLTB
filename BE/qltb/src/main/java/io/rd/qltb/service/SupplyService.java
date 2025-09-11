package io.rd.qltb.service;

import io.rd.qltb.domain.Supply;
import io.rd.qltb.domain.SupplyGroup;
import io.rd.qltb.events.BeforeDeleteSupply;
import io.rd.qltb.events.BeforeDeleteSupplyGroup;
import io.rd.qltb.model.SupplyDTO;
import io.rd.qltb.repos.SupplyGroupRepository;
import io.rd.qltb.repos.SupplyRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final SupplyGroupRepository supplyGroupRepository;
    private final ApplicationEventPublisher publisher;

    public SupplyService(final SupplyRepository supplyRepository,
            final SupplyGroupRepository supplyGroupRepository,
            final ApplicationEventPublisher publisher) {
        this.supplyRepository = supplyRepository;
        this.supplyGroupRepository = supplyGroupRepository;
        this.publisher = publisher;
    }

    public List<SupplyDTO> findAll() {
        final List<Supply> supplies = supplyRepository.findAll(Sort.by("id"));
        return supplies.stream()
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .toList();
    }

    public SupplyDTO get(final Integer id) {
        return supplyRepository.findById(id)
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final SupplyDTO supplyDTO) {
        final Supply supply = new Supply();
        mapToEntity(supplyDTO, supply);
        return supplyRepository.save(supply).getId();
    }

    public void update(final Integer id, final SupplyDTO supplyDTO) {
        final Supply supply = supplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyDTO, supply);
        supplyRepository.save(supply);
    }

    public void delete(final Integer id) {
        final Supply supply = supplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteSupply(id));
        supplyRepository.delete(supply);
    }

    private SupplyDTO mapToDTO(final Supply supply, final SupplyDTO dto) {
        dto.setId(supply.getId());
        dto.setCode(supply.getCode());
        dto.setName(supply.getName());
        dto.setDescription(supply.getDescription());
        dto.setSource(supply.getSource());
        dto.setCreatedAt(supply.getCreatedAt());
        dto.setUpdatedAt(supply.getUpdatedAt());
        dto.setCreatedBy(supply.getCreatedBy());
        dto.setUpdatedBy(supply.getUpdatedBy());
        dto.setStatus(supply.getStatus());

        // Sao chép SupplyGroup có kiểm soát
        if (supply.getGroup() != null) {
            SupplyGroup groupCopy = new SupplyGroup();
            groupCopy.setId(supply.getGroup().getId());
            groupCopy.setCode(supply.getGroup().getCode());
            groupCopy.setName(supply.getGroup().getName());
            groupCopy.setDescription(supply.getGroup().getDescription());
            groupCopy.setCreatedAt(supply.getGroup().getCreatedAt());
            groupCopy.setUpdatedAt(supply.getGroup().getUpdatedAt());
            groupCopy.setCreatedBy(supply.getGroup().getCreatedBy());
            groupCopy.setUpdatedBy(supply.getGroup().getUpdatedBy());
            groupCopy.setStatus(supply.getGroup().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            groupCopy.setGroupSupplies(null);

            dto.setGroup(groupCopy);
        } else {
            dto.setGroup(null);
        }

        return dto;
    }


    private Supply mapToEntity(final SupplyDTO supplyDTO, final Supply supply) {
        supply.setCode(supplyDTO.getCode());
        supply.setName(supplyDTO.getName());
        supply.setDescription(supplyDTO.getDescription());
        supply.setSource(supplyDTO.getSource());
        supply.setCreatedAt(supplyDTO.getCreatedAt());
        supply.setUpdatedAt(supplyDTO.getUpdatedAt());
        supply.setCreatedBy(supplyDTO.getCreatedBy());
        supply.setUpdatedBy(supplyDTO.getUpdatedBy());
        supply.setStatus(supplyDTO.getStatus());
        final SupplyGroup group = supplyDTO.getGroup() == null ? null : supplyGroupRepository.findById(supplyDTO.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("group not found"));
        supply.setGroup(group);
        return supply;
    }

    @EventListener(BeforeDeleteSupplyGroup.class)
    public void on(final BeforeDeleteSupplyGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final Supply groupSupply = supplyRepository.findFirstByGroupId(event.getId());
        if (groupSupply != null) {
            referencedException.setKey("supplyGroup.supply.group.referenced");
            referencedException.addParam(groupSupply.getId());
            throw referencedException;
        }
    }

}
