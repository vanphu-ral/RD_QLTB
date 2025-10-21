package io.rd.qltb.service;

import io.rd.qltb.domain.PlanType;
import io.rd.qltb.events.BeforeDeletePlanType;
import io.rd.qltb.model.PlanTypeDTO;
import io.rd.qltb.repos.PlanTypeRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanTypeService {

    private final PlanTypeRepository planTypeRepository;
    private final ApplicationEventPublisher publisher;

    public PlanTypeService(final PlanTypeRepository planTypeRepository,
            final ApplicationEventPublisher publisher) {
        this.planTypeRepository = planTypeRepository;
        this.publisher = publisher;
    }

    public List<PlanTypeDTO> findAll() {
        final List<PlanType> planTypes = planTypeRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return planTypes.stream()
                .map(planType -> mapToDTO(planType, new PlanTypeDTO()))
                .toList();
    }

    public PlanTypeDTO get(final Long id) {
        return planTypeRepository.findById(id)
                .map(planType -> mapToDTO(planType, new PlanTypeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanTypeDTO planTypeDTO) {
        final PlanType planType = new PlanType();
        mapToEntity(planTypeDTO, planType);
        return planTypeRepository.save(planType).getId();
    }

    public void update(final Long id, final PlanTypeDTO planTypeDTO) {
        final PlanType planType = planTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planTypeDTO, planType);
        planTypeRepository.save(planType);
    }

    public void delete(final Long id) {
        final PlanType planType = planTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePlanType(id));
        planTypeRepository.delete(planType);
    }

    private PlanTypeDTO mapToDTO(final PlanType planType, final PlanTypeDTO planTypeDTO) {
        planTypeDTO.setId(planType.getId());
        planTypeDTO.setCode(planType.getCode());
        planTypeDTO.setName(planType.getName());
        planTypeDTO.setDescription(planType.getDescription());
        planTypeDTO.setCreatedAt(planType.getCreatedAt());
        planTypeDTO.setUpdatedAt(planType.getUpdatedAt());
        planTypeDTO.setCreatedBy(planType.getCreatedBy());
        planTypeDTO.setUpdatedBy(planType.getUpdatedBy());
        planTypeDTO.setStatus(planType.getStatus());
        return planTypeDTO;
    }

    private PlanType mapToEntity(final PlanTypeDTO planTypeDTO, final PlanType planType) {
        planType.setCode(planTypeDTO.getCode());
        planType.setDescription(planTypeDTO.getDescription());
        planType.setName(planTypeDTO.getName());
        planType.setCreatedAt(planTypeDTO.getCreatedAt());
        planType.setUpdatedAt(planTypeDTO.getUpdatedAt());
        planType.setCreatedBy(planTypeDTO.getCreatedBy());
        planType.setUpdatedBy(planTypeDTO.getUpdatedBy());
        planType.setStatus(planTypeDTO.getStatus());
        return planType;
    }

}
