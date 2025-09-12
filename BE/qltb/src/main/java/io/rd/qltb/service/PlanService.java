package io.rd.qltb.service;

import io.rd.qltb.domain.Plan;
import io.rd.qltb.domain.PlanType;
import io.rd.qltb.events.BeforeDeletePlan;
import io.rd.qltb.events.BeforeDeletePlanType;
import io.rd.qltb.model.PlanDTO;
import io.rd.qltb.repos.PlanRepository;
import io.rd.qltb.repos.PlanTypeRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanTypeRepository planTypeRepository;
    private final ApplicationEventPublisher publisher;

    public PlanService(final PlanRepository planRepository,
            final PlanTypeRepository planTypeRepository,
            final ApplicationEventPublisher publisher) {
        this.planRepository = planRepository;
        this.planTypeRepository = planTypeRepository;
        this.publisher = publisher;
    }

    public List<PlanDTO> findAll() {
        final List<Plan> plans = planRepository.findAll(Sort.by("id"));
        return plans.stream()
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .toList();
    }

    public PlanDTO get(final Long id) {
        return planRepository.findById(id)
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanDTO planDTO) {
        final Plan plan = new Plan();
        mapToEntity(planDTO, plan);
        return planRepository.save(plan).getId();
    }

    public void update(final Long id, final PlanDTO planDTO) {
        final Plan plan = planRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planDTO, plan);
        planRepository.save(plan);
    }

    public void delete(final Long id) {
        final Plan plan = planRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePlan(id));
        planRepository.delete(plan);
    }

    private PlanDTO mapToDTO(final Plan plan, final PlanDTO dto) {
        dto.setId(plan.getId());
        dto.setName(plan.getName());
        dto.setFactoryId(plan.getFactoryId());
        dto.setBranchId(plan.getBranchId());
        dto.setFrequency(plan.getFrequency());
        dto.setPlanNumber(plan.getPlanNumber());
        dto.setDescription(plan.getDescription());
        dto.setCreatedBy(plan.getCreatedBy());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        dto.setUpdatedBy(plan.getUpdatedBy());
        dto.setStatus(plan.getStatus());

        // Sao chép PlanType có kiểm soát
        if (plan.getPlanType() != null) {
            PlanType planTypeCopy = new PlanType();
            planTypeCopy.setId(plan.getPlanType().getId());
            planTypeCopy.setCode(plan.getPlanType().getCode());
            planTypeCopy.setName(plan.getPlanType().getName());
            planTypeCopy.setCreatedAt(plan.getPlanType().getCreatedAt());
            planTypeCopy.setUpdatedAt(plan.getPlanType().getUpdatedAt());
            planTypeCopy.setCreatedBy(plan.getPlanType().getCreatedBy());
            planTypeCopy.setUpdatedBy(plan.getPlanType().getUpdatedBy());
            planTypeCopy.setStatus(plan.getPlanType().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            planTypeCopy.setPlanTypePlans(null);

            dto.setPlanType(planTypeCopy);
        } else {
            dto.setPlanType(null);
        }

        return dto;
    }


    private Plan mapToEntity(final PlanDTO planDTO, final Plan plan) {
        plan.setName(planDTO.getName());
        plan.setFactoryId(planDTO.getFactoryId());
        plan.setBranchId(planDTO.getBranchId());
        plan.setFrequency(planDTO.getFrequency());
        plan.setPlanNumber(planDTO.getPlanNumber());
        plan.setDescription(planDTO.getDescription());
        plan.setCreatedBy(planDTO.getCreatedBy());
        plan.setCreatedAt(planDTO.getCreatedAt());
        plan.setUpdatedAt(planDTO.getUpdatedAt());
        plan.setUpdatedBy(planDTO.getUpdatedBy());
        plan.setStatus(planDTO.getStatus());
        final PlanType planType = planDTO.getPlanType() == null ? null : planTypeRepository.findById(planDTO.getPlanType().getId())
                .orElseThrow(() -> new NotFoundException("planType not found"));
        plan.setPlanType(planType);
        return plan;
    }

    @EventListener(BeforeDeletePlanType.class)
    public void on(final BeforeDeletePlanType event) {
        final ReferencedException referencedException = new ReferencedException();
        final Plan planTypePlan = planRepository.findFirstByPlanTypeId(event.getId());
        if (planTypePlan != null) {
            referencedException.setKey("planType.plan.planType.referenced");
            referencedException.addParam(planTypePlan.getId());
            throw referencedException;
        }
    }

}
