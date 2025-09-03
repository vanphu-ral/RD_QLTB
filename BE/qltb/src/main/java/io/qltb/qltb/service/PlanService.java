package io.qltb.qltb.service;

import io.qltb.qltb.domain.Plan;
import io.qltb.qltb.domain.PlanType;
import io.qltb.qltb.events.BeforeDeletePlan;
import io.qltb.qltb.events.BeforeDeletePlanType;
import io.qltb.qltb.model.PlanDTO;
import io.qltb.qltb.repos.PlanRepository;
import io.qltb.qltb.repos.PlanTypeRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
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

    private PlanDTO mapToDTO(final Plan plan, final PlanDTO planDTO) {
        planDTO.setId(plan.getId());
        planDTO.setName(plan.getName());
        planDTO.setFactoryId(plan.getFactoryId());
        planDTO.setBranchId(plan.getBranchId());
        planDTO.setFrequency(plan.getFrequency());
        planDTO.setPlanNumber(plan.getPlanNumber());
        planDTO.setNote(plan.getNote());
        planDTO.setCreatedBy(plan.getCreatedBy());
        planDTO.setCreatedAt(plan.getCreatedAt());
        planDTO.setUpdatedAt(plan.getUpdatedAt());
        planDTO.setUpdatedBy(plan.getUpdatedBy());
        planDTO.setStatus(plan.getStatus());
        planDTO.setPlanType(plan.getPlanType() == null ? null : plan.getPlanType());
        return planDTO;
    }

    private Plan mapToEntity(final PlanDTO planDTO, final Plan plan) {
        plan.setName(planDTO.getName());
        plan.setFactoryId(planDTO.getFactoryId());
        plan.setBranchId(planDTO.getBranchId());
        plan.setFrequency(planDTO.getFrequency());
        plan.setPlanNumber(planDTO.getPlanNumber());
        plan.setNote(planDTO.getNote());
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
