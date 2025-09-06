package io.qltb.qltb.service;

import io.qltb.qltb.domain.Supply;
import io.qltb.qltb.domain.SupplyGroup;
import io.qltb.qltb.events.BeforeDeleteSupply;
import io.qltb.qltb.events.BeforeDeleteSupplyGroup;
import io.qltb.qltb.model.SupplyDTO;
import io.qltb.qltb.repos.SupplyGroupRepository;
import io.qltb.qltb.repos.SupplyRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
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

    public SupplyDTO get(final Long id) {
        return supplyRepository.findById(id)
                .map(supply -> mapToDTO(supply, new SupplyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SupplyDTO supplyDTO) {
        final Supply supply = new Supply();
        mapToEntity(supplyDTO, supply);
        return supplyRepository.save(supply).getId();
    }

    public void update(final Long id, final SupplyDTO supplyDTO) {
        final Supply supply = supplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyDTO, supply);
        supplyRepository.save(supply);
    }

    public void delete(final Long id) {
        final Supply supply = supplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteSupply(id));
        supplyRepository.delete(supply);
    }

    private SupplyDTO mapToDTO(final Supply supply, final SupplyDTO supplyDTO) {
        supplyDTO.setId(supply.getId());
        supplyDTO.setCode(supply.getCode());
        supplyDTO.setName(supply.getName());
        supplyDTO.setQuantity(supply.getQuantity());
        supplyDTO.setDescription(supply.getDescription());
        // supplyDTO.setSapCode(supply.getSapCode()); // Nếu cần dùng, mở lại dòng này
        supplyDTO.setSource(supply.getSource());
        supplyDTO.setCreatedAt(supply.getCreatedAt());
        supplyDTO.setUpdatedAt(supply.getUpdatedAt());
        supplyDTO.setCreatedBy(supply.getCreatedBy());
        supplyDTO.setUpdatedBy(supply.getUpdatedBy());
        supplyDTO.setStatus(supply.getStatus());

        if (supply.getGroup() != null) {
            SupplyGroup groupCopy = new SupplyGroup();
            groupCopy.setId(supply.getGroup().getId());
            groupCopy.setCode(supply.getGroup().getCode());
            groupCopy.setName(supply.getGroup().getName());
            groupCopy.setDescription(supply.getGroup().getDescription());
            groupCopy.setStatus(supply.getGroup().getStatus());
            groupCopy.setCreatedAt(supply.getGroup().getCreatedAt());
            groupCopy.setUpdatedAt(supply.getGroup().getUpdatedAt());
            groupCopy.setCreatedBy(supply.getGroup().getCreatedBy());
            groupCopy.setUpdatedBy(supply.getGroup().getUpdatedBy());

            // Loại bỏ quan hệ con
            groupCopy.setGroupSupplies(null);

            supplyDTO.setGroup(groupCopy);
        } else {
            supplyDTO.setGroup(null);
        }

        return supplyDTO;
    }


    private Supply mapToEntity(final SupplyDTO supplyDTO, final Supply supply) {
        supply.setCode(supplyDTO.getCode());
        supply.setName(supplyDTO.getName());
        supply.setQuantity(supplyDTO.getQuantity());
        supply.setDescription(supplyDTO.getDescription());
//        supply.setSapCode(supplyDTO.getSapCode());
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

    public boolean codeExists(final String code) {
        return supplyRepository.existsByCodeIgnoreCase(code);
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
