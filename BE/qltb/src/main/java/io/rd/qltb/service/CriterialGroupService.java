package io.rd.qltb.service;

import io.rd.qltb.domain.CriterialGroup;
import io.rd.qltb.events.BeforeDeleteCriterialGroup;
import io.rd.qltb.model.CriterialGroupDTO;
import io.rd.qltb.repos.CriterialGroupRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CriterialGroupService {

    private final CriterialGroupRepository criterialGroupRepository;
    private final ApplicationEventPublisher publisher;

    public CriterialGroupService(final CriterialGroupRepository criterialGroupRepository,
            final ApplicationEventPublisher publisher) {
        this.criterialGroupRepository = criterialGroupRepository;
        this.publisher = publisher;
    }

    public List<CriterialGroupDTO> findAll() {
        final List<CriterialGroup> criterialGroups = criterialGroupRepository.findAll(Sort.by("id"));
        return criterialGroups.stream()
                .map(criterialGroup -> mapToDTO(criterialGroup, new CriterialGroupDTO()))
                .toList();
    }

    public CriterialGroupDTO get(final Long id) {
        return criterialGroupRepository.findById(id)
                .map(criterialGroup -> mapToDTO(criterialGroup, new CriterialGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CriterialGroupDTO criterialGroupDTO) {
        final CriterialGroup criterialGroup = new CriterialGroup();
        mapToEntity(criterialGroupDTO, criterialGroup);
        return criterialGroupRepository.save(criterialGroup).getId();
    }

    public void update(final Long id, final CriterialGroupDTO criterialGroupDTO) {
        final CriterialGroup criterialGroup = criterialGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(criterialGroupDTO, criterialGroup);
        criterialGroupRepository.save(criterialGroup);
    }

    public void delete(final Long id) {
        final CriterialGroup criterialGroup = criterialGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCriterialGroup(id));
        criterialGroupRepository.delete(criterialGroup);
    }

    private CriterialGroupDTO mapToDTO(final CriterialGroup criterialGroup,
            final CriterialGroupDTO criterialGroupDTO) {
        criterialGroupDTO.setId(criterialGroup.getId());
        criterialGroupDTO.setCode(criterialGroup.getCode());
        criterialGroupDTO.setName(criterialGroup.getName());
        criterialGroupDTO.setDescription(criterialGroup.getDescription());
        criterialGroupDTO.setCreatedAt(criterialGroup.getCreatedAt());
        criterialGroupDTO.setUpdatedAt(criterialGroup.getUpdatedAt());
        criterialGroupDTO.setCreatedBy(criterialGroup.getCreatedBy());
        criterialGroupDTO.setUpdatedBy(criterialGroup.getUpdatedBy());
        criterialGroupDTO.setStatus(criterialGroup.getStatus());
        return criterialGroupDTO;
    }

    private CriterialGroup mapToEntity(final CriterialGroupDTO criterialGroupDTO,
            final CriterialGroup criterialGroup) {
        criterialGroup.setCode(criterialGroupDTO.getCode());
        criterialGroup.setName(criterialGroupDTO.getName());
        criterialGroup.setDescription(criterialGroupDTO.getDescription());
        criterialGroup.setCreatedAt(criterialGroupDTO.getCreatedAt());
        criterialGroup.setUpdatedAt(criterialGroupDTO.getUpdatedAt());
        criterialGroup.setCreatedBy(criterialGroupDTO.getCreatedBy());
        criterialGroup.setUpdatedBy(criterialGroupDTO.getUpdatedBy());
        criterialGroup.setStatus(criterialGroupDTO.getStatus());
        return criterialGroup;
    }

}
