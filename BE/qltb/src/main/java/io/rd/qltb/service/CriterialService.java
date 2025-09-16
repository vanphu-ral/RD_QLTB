package io.rd.qltb.service;

import io.rd.qltb.domain.Criterial;
import io.rd.qltb.domain.CriterialGroup;
import io.rd.qltb.events.BeforeDeleteCriterial;
import io.rd.qltb.events.BeforeDeleteCriterialGroup;
import io.rd.qltb.model.CriterialDTO;
import io.rd.qltb.model.SupplyDetailDTO;
import io.rd.qltb.repos.CriterialGroupRepository;
import io.rd.qltb.repos.CriterialRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CriterialService {

    private final CriterialRepository criterialRepository;
    private final CriterialGroupRepository criterialGroupRepository;
    private final ApplicationEventPublisher publisher;

    public CriterialService(final CriterialRepository criterialRepository,
            final CriterialGroupRepository criterialGroupRepository,
            final ApplicationEventPublisher publisher) {
        this.criterialRepository = criterialRepository;
        this.criterialGroupRepository = criterialGroupRepository;
        this.publisher = publisher;
    }

    public List<CriterialDTO> findAll() {
        final List<Criterial> criterials = criterialRepository.findAll(Sort.by("id"));
        return criterials.stream()
                .map(criterial -> mapToDTO(criterial, new CriterialDTO()))
                .toList();
    }

    public List<CriterialDTO> getByCriterialGroup(Long criterialGroupId) {
        return criterialRepository.findByCriterialGroupId(criterialGroupId).stream()
                .map(s -> mapToDTO(s, new CriterialDTO()))
                .collect(Collectors.toList());
    }

    public CriterialDTO get(final Long id) {
        return criterialRepository.findById(id)
                .map(criterial -> mapToDTO(criterial, new CriterialDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CriterialDTO criterialDTO) {
        final Criterial criterial = new Criterial();
        mapToEntity(criterialDTO, criterial);
        return criterialRepository.save(criterial).getId();
    }

    public void update(final Long id, final CriterialDTO criterialDTO) {
        final Criterial criterial = criterialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(criterialDTO, criterial);
        criterialRepository.save(criterial);
    }

    public void delete(final Long id) {
        final Criterial criterial = criterialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCriterial(id));
        criterialRepository.delete(criterial);
    }

    private CriterialDTO mapToDTO(final Criterial criterial, final CriterialDTO criterialDTO) {
        criterialDTO.setId(criterial.getId());
        criterialDTO.setCode(criterial.getCode());
        criterialDTO.setName(criterial.getName());
        criterialDTO.setDetail(criterial.getDetail());
        criterialDTO.setDescription(criterial.getDescription());
        criterialDTO.setFrequency(criterial.getFrequency());
        criterialDTO.setCreatedAt(criterial.getCreatedAt());
        criterialDTO.setUpdatedAt(criterial.getUpdatedAt());
        criterialDTO.setCreatedBy(criterial.getCreatedBy());
        criterialDTO.setUpdatedBy(criterial.getUpdatedBy());
        criterialDTO.setStatus(criterial.getStatus());

        // Sao chép CriterialGroup có kiểm soát
        if (criterial.getCriterialGroup() != null) {
            CriterialGroup groupCopy = new CriterialGroup();
            groupCopy.setId(criterial.getCriterialGroup().getId());
            groupCopy.setCode(criterial.getCriterialGroup().getCode());
            groupCopy.setName(criterial.getCriterialGroup().getName());
            groupCopy.setStatus(criterial.getCriterialGroup().getStatus());
            groupCopy.setCreatedAt(criterial.getCriterialGroup().getCreatedAt());
            groupCopy.setUpdatedAt(criterial.getCriterialGroup().getUpdatedAt());
            groupCopy.setCreatedBy(criterial.getCriterialGroup().getCreatedBy());
            groupCopy.setUpdatedBy(criterial.getCriterialGroup().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp
            groupCopy.setCriterialGroupCriterials(null);

            criterialDTO.setCriterialGroup(groupCopy);
        } else {
            criterialDTO.setCriterialGroup(null);
        }

        return criterialDTO;
    }


    private Criterial mapToEntity(final CriterialDTO criterialDTO, final Criterial criterial) {
        criterial.setCode(criterialDTO.getCode());
        criterial.setName(criterialDTO.getName());
        criterial.setDetail(criterialDTO.getDetail());
        criterial.setDescription(criterialDTO.getDescription());
        criterial.setFrequency(criterialDTO.getFrequency());
        criterial.setCreatedAt(criterialDTO.getCreatedAt());
        criterial.setUpdatedAt(criterialDTO.getUpdatedAt());
        criterial.setCreatedBy(criterialDTO.getCreatedBy());
        criterial.setUpdatedBy(criterialDTO.getUpdatedBy());
        criterial.setStatus(criterialDTO.getStatus());
        final CriterialGroup criterialGroup = criterialDTO.getCriterialGroup() == null ? null : criterialGroupRepository.findById(criterialDTO.getCriterialGroup().getId())
                .orElseThrow(() -> new NotFoundException("criterialGroup not found"));
        criterial.setCriterialGroup(criterialGroup);
        return criterial;
    }

    @EventListener(BeforeDeleteCriterialGroup.class)
    public void on(final BeforeDeleteCriterialGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final Criterial criterialGroupCriterial = criterialRepository.findFirstByCriterialGroupId(event.getId());
        if (criterialGroupCriterial != null) {
            referencedException.setKey("criterialGroup.criterial.criterialGroup.referenced");
            referencedException.addParam(criterialGroupCriterial.getId());
            throw referencedException;
        }
    }

}
