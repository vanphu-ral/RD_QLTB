package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeletePlan;
import io.rd.qltb.events.BeforeDeletePlanType;
import io.rd.qltb.model.PlanDTO;
import io.rd.qltb.repos.*;
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
    private final FactoryRepository factoryRepository;
    private final BranchRepository branchRepository;
    private final ApprovalWorkflowRepository approvalWorkflowRepository;
    private final ApplicationEventPublisher publisher;

    public PlanService(final PlanRepository planRepository,
            final PlanTypeRepository planTypeRepository,
            final FactoryRepository factoryRepository,
            final BranchRepository branchRepository,
            final ApprovalWorkflowRepository approvalWorkflowRepository,
            final ApplicationEventPublisher publisher) {
        this.planRepository = planRepository;
        this.planTypeRepository = planTypeRepository;
        this.factoryRepository = factoryRepository;
        this.branchRepository = branchRepository;
        this.approvalWorkflowRepository = approvalWorkflowRepository;
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
        dto.setCode(plan.getCode());
        dto.setName(plan.getName());
        dto.setFrequency(plan.getFrequency());
        dto.setPlanNumber(plan.getPlanNumber());
        dto.setUserPerformer(plan.getUserPerformer());
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

        if(plan.getFactory() != null) {
            Factory factoryCopy = new Factory();
            factoryCopy.setId(plan.getFactory().getId());
            factoryCopy.setCode(plan.getFactory().getCode());
            factoryCopy.setName(plan.getFactory().getName());
            factoryCopy.setStatus(plan.getFactory().getStatus());

            factoryCopy.setFactoryBranches(null);

            dto.setFactory(factoryCopy);
        }else {
            dto.setFactory(null);
        }

        if (plan.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(plan.getBranch().getId());
            branchCopy.setCode(plan.getBranch().getCode());
            branchCopy.setName(plan.getBranch().getName());
            branchCopy.setDescription(plan.getBranch().getDescription());
            branchCopy.setCreatedAt(plan.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(plan.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(plan.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(plan.getBranch().getUpdatedBy());
            branchCopy.setStatus(plan.getBranch().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);
            branchCopy.setFactory(null);

            dto.setBranch(branchCopy);
        } else {
            dto.setBranch(null);
        }

        if(plan.getApprovalWorkflow() != null) {
            ApprovalWorkflow approvalWorkflowCopy = new ApprovalWorkflow();
            approvalWorkflowCopy.setId(plan.getApprovalWorkflow().getId());
            approvalWorkflowCopy.setCode(plan.getApprovalWorkflow().getCode());
            approvalWorkflowCopy.setName(plan.getApprovalWorkflow().getName());
            approvalWorkflowCopy.setStatus(plan.getApprovalWorkflow().getStatus());

            approvalWorkflowCopy.setWorkflowApprovalGroups(null);

            dto.setApprovalWorkflow(approvalWorkflowCopy);
        } else {
            dto.setApprovalWorkflow(null);
        }

        return dto;
    }


    private Plan mapToEntity(final PlanDTO planDTO, final Plan plan) {
        plan.setCode(planDTO.getCode());
        plan.setName(planDTO.getName());
        plan.setFrequency(planDTO.getFrequency());
        plan.setPlanNumber(planDTO.getPlanNumber());
        plan.setUserPerformer(planDTO.getUserPerformer());
        plan.setDescription(planDTO.getDescription());
        plan.setCreatedBy(planDTO.getCreatedBy());
        plan.setCreatedAt(planDTO.getCreatedAt());
        plan.setUpdatedAt(planDTO.getUpdatedAt());
        plan.setUpdatedBy(planDTO.getUpdatedBy());
        plan.setStatus(planDTO.getStatus());
        final PlanType planType = planDTO.getPlanType() == null ? null : planTypeRepository.findById(planDTO.getPlanType().getId())
                .orElseThrow(() -> new NotFoundException("planType not found"));
        plan.setPlanType(planType);

        final Factory factory = planDTO.getFactory() == null ? null : factoryRepository.findById(planDTO.getFactory().getId())
                .orElseThrow(() -> new NotFoundException("factory not found"));
        plan.setFactory(factory);

        final Branch branch = planDTO.getBranch() == null ? null : branchRepository.findById(planDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        plan.setBranch(branch);

        final ApprovalWorkflow approvalWorkflow = planDTO.getApprovalWorkflow() == null ? null :
                approvalWorkflowRepository.findById(planDTO.getApprovalWorkflow().getId())
                        .orElseThrow(() -> new NotFoundException("approvalWorkflow not found"));
        plan.setApprovalWorkflow(approvalWorkflow);

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
