package io.qltb.qltb.service;

import io.qltb.qltb.domain.PlanType;
import io.qltb.qltb.events.BeforeDeletePlanType;
import io.qltb.qltb.model.PlanTypeDTO;
import io.qltb.qltb.repos.PlanTypeRepository;
import io.qltb.qltb.util.NotFoundException;
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
        final List<PlanType> planTypes = planTypeRepository.findAll(Sort.by("id"));
        return planTypes.stream()
                .map(planType -> mapToDTO(planType, new PlanTypeDTO()))
                .toList();
    }

    public PlanTypeDTO get(final Integer id) {
        return planTypeRepository.findById(id)
                .map(planType -> mapToDTO(planType, new PlanTypeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final PlanTypeDTO planTypeDTO) {
        final PlanType planType = new PlanType();
        mapToEntity(planTypeDTO, planType);
        return planTypeRepository.save(planType).getId();
    }

    public void update(final Integer id, final PlanTypeDTO planTypeDTO) {
        final PlanType planType = planTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planTypeDTO, planType);
        planTypeRepository.save(planType);
    }

    public void delete(final Integer id) {
        final PlanType planType = planTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePlanType(id));
        planTypeRepository.delete(planType);
    }

    private PlanTypeDTO mapToDTO(final PlanType planType, final PlanTypeDTO planTypeDTO) {
        planTypeDTO.setId(planType.getId());
        planTypeDTO.setCode(planType.getCode());
        planTypeDTO.setName(planType.getName());
        planTypeDTO.setCreatedAt(planType.getCreatedAt());
        planTypeDTO.setUpdatedAt(planType.getUpdatedAt());
        planTypeDTO.setCreatedBy(planType.getCreatedBy());
        planTypeDTO.setUpdatedBy(planType.getUpdatedBy());
        planTypeDTO.setStatus(planType.getStatus());
        return planTypeDTO;
    }

    private PlanType mapToEntity(final PlanTypeDTO planTypeDTO, final PlanType planType) {
        planType.setCode(planTypeDTO.getCode());
        planType.setName(planTypeDTO.getName());
        planType.setCreatedAt(planTypeDTO.getCreatedAt());
        planType.setUpdatedAt(planTypeDTO.getUpdatedAt());
        planType.setCreatedBy(planTypeDTO.getCreatedBy());
        planType.setUpdatedBy(planTypeDTO.getUpdatedBy());
        planType.setStatus(planTypeDTO.getStatus());
        return planType;
    }

    public boolean codeExists(final String code) {
        return planTypeRepository.existsByCodeIgnoreCase(code);
    }

}
