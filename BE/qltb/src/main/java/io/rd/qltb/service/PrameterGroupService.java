package io.rd.qltb.service;

import io.rd.qltb.domain.PrameterGroup;
import io.rd.qltb.events.BeforeDeletePrameterGroup;
import io.rd.qltb.model.PrameterGroupDTO;
import io.rd.qltb.repos.PrameterGroupRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PrameterGroupService {

    private final PrameterGroupRepository prameterGroupRepository;
    private final ApplicationEventPublisher publisher;

    public PrameterGroupService(final PrameterGroupRepository prameterGroupRepository,
            final ApplicationEventPublisher publisher) {
        this.prameterGroupRepository = prameterGroupRepository;
        this.publisher = publisher;
    }

    public List<PrameterGroupDTO> findAll() {
        final List<PrameterGroup> prameterGroups = prameterGroupRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return prameterGroups.stream()
                .map(prameterGroup -> mapToDTO(prameterGroup, new PrameterGroupDTO()))
                .toList();
    }

    public PrameterGroupDTO get(final Long id) {
        return prameterGroupRepository.findById(id)
                .map(prameterGroup -> mapToDTO(prameterGroup, new PrameterGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PrameterGroupDTO prameterGroupDTO) {
        final PrameterGroup prameterGroup = new PrameterGroup();
        mapToEntity(prameterGroupDTO, prameterGroup);
        return prameterGroupRepository.save(prameterGroup).getId();
    }

    public void update(final Long id, final PrameterGroupDTO prameterGroupDTO) {
        final PrameterGroup prameterGroup = prameterGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(prameterGroupDTO, prameterGroup);
        prameterGroupRepository.save(prameterGroup);
    }

    public void delete(final Long id) {
        final PrameterGroup prameterGroup = prameterGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePrameterGroup(id));
        prameterGroupRepository.delete(prameterGroup);
    }

    private PrameterGroupDTO mapToDTO(final PrameterGroup prameterGroup,
            final PrameterGroupDTO prameterGroupDTO) {
        prameterGroupDTO.setId(prameterGroup.getId());
        prameterGroupDTO.setCode(prameterGroup.getCode());
        prameterGroupDTO.setName(prameterGroup.getName());
        prameterGroupDTO.setDescription(prameterGroup.getDescription());
        prameterGroupDTO.setCreatedAt(prameterGroup.getCreatedAt());
        prameterGroupDTO.setUpdatedAt(prameterGroup.getUpdatedAt());
        prameterGroupDTO.setCreatedBy(prameterGroup.getCreatedBy());
        prameterGroupDTO.setUpdatedBy(prameterGroup.getUpdatedBy());
        prameterGroupDTO.setStatus(prameterGroup.getStatus());
        return prameterGroupDTO;
    }

    private PrameterGroup mapToEntity(final PrameterGroupDTO prameterGroupDTO,
            final PrameterGroup prameterGroup) {
        prameterGroup.setCode(prameterGroupDTO.getCode());
        prameterGroup.setName(prameterGroupDTO.getName());
        prameterGroup.setDescription(prameterGroupDTO.getDescription());
        prameterGroup.setCreatedAt(prameterGroupDTO.getCreatedAt());
        prameterGroup.setUpdatedAt(prameterGroupDTO.getUpdatedAt());
        prameterGroup.setCreatedBy(prameterGroupDTO.getCreatedBy());
        prameterGroup.setUpdatedBy(prameterGroupDTO.getUpdatedBy());
        prameterGroup.setStatus(prameterGroupDTO.getStatus());
        return prameterGroup;
    }

}
