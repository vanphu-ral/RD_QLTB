package io.rd.qltb.service;

import io.rd.qltb.config.GlobalConfig;
import io.rd.qltb.domain.SupplyGroup;
import io.rd.qltb.events.BeforeDeleteSupplyGroup;
import io.rd.qltb.model.SupplyGroupDTO;
import io.rd.qltb.repos.SupplyGroupRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class SupplyGroupService {

    private final SupplyGroupRepository supplyGroupRepository;
    private final ApplicationEventPublisher publisher;
private final GlobalConfig globalConfig;
    public SupplyGroupService(final SupplyGroupRepository supplyGroupRepository,
                              final ApplicationEventPublisher publisher, GlobalConfig globalConfig) {
        this.supplyGroupRepository = supplyGroupRepository;
        this.publisher = publisher;
        this.globalConfig = globalConfig;
    }

    public List<SupplyGroupDTO> findAll() {
        final List<SupplyGroup> supplyGroups = supplyGroupRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return supplyGroups.stream()
                .map(supplyGroup -> mapToDTO(supplyGroup, new SupplyGroupDTO()))
                .toList();
    }

    public SupplyGroupDTO get(final Long id) {
        return supplyGroupRepository.findById(id)
                .map(supplyGroup -> mapToDTO(supplyGroup, new SupplyGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SupplyGroupDTO supplyGroupDTO) {
        final SupplyGroup supplyGroup = new SupplyGroup();
        mapToEntity(supplyGroupDTO, supplyGroup);
        SupplyGroup savedSupplyGroup = supplyGroupRepository.save(supplyGroup);
        savedSupplyGroup.setCode(supplyGroupDTO.getCode() +"-"+globalConfig.createNumberPrefix(savedSupplyGroup.getId(), 4));
        return supplyGroupRepository.save(supplyGroup).getId();
    }

    public void update(final Long id, final SupplyGroupDTO supplyGroupDTO) {
        final SupplyGroup supplyGroup = supplyGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(supplyGroupDTO, supplyGroup);
        supplyGroupRepository.save(supplyGroup);
    }

    public void delete(final Long id) {
        final SupplyGroup supplyGroup = supplyGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteSupplyGroup(id));
        supplyGroupRepository.delete(supplyGroup);
    }

    private SupplyGroupDTO mapToDTO(final SupplyGroup supplyGroup,
            final SupplyGroupDTO supplyGroupDTO) {
        supplyGroupDTO.setId(supplyGroup.getId());
        supplyGroupDTO.setCode(supplyGroup.getCode());
        supplyGroupDTO.setName(supplyGroup.getName());
        supplyGroupDTO.setDescription(supplyGroup.getDescription());
        supplyGroupDTO.setCreatedAt(supplyGroup.getCreatedAt());
        supplyGroupDTO.setUpdatedAt(supplyGroup.getUpdatedAt());
        supplyGroupDTO.setCreatedBy(supplyGroup.getCreatedBy());
        supplyGroupDTO.setUpdatedBy(supplyGroup.getUpdatedBy());
        supplyGroupDTO.setStatus(supplyGroup.getStatus());
        return supplyGroupDTO;
    }

    private SupplyGroup mapToEntity(final SupplyGroupDTO supplyGroupDTO,
            final SupplyGroup supplyGroup) {
        supplyGroup.setCode(supplyGroupDTO.getCode());
        supplyGroup.setName(supplyGroupDTO.getName());
        supplyGroup.setDescription(supplyGroupDTO.getDescription());
        supplyGroup.setCreatedAt(supplyGroupDTO.getCreatedAt());
        supplyGroup.setUpdatedAt(supplyGroupDTO.getUpdatedAt());
        supplyGroup.setCreatedBy(supplyGroupDTO.getCreatedBy());
        supplyGroup.setUpdatedBy(supplyGroupDTO.getUpdatedBy());
        supplyGroup.setStatus(supplyGroupDTO.getStatus());
        return supplyGroup;
    }

}
