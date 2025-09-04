package io.qltb.qltb.service;

import io.qltb.qltb.domain.Branch;
import io.qltb.qltb.domain.PlanTarget;
import io.qltb.qltb.events.BeforeDeleteBranch;
import io.qltb.qltb.events.BeforeDeletePlanTarget;
import io.qltb.qltb.model.PlanTargetDTO;
import io.qltb.qltb.repos.BranchRepository;
import io.qltb.qltb.repos.PlanTargetRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanTargetService {

    private final PlanTargetRepository planTargetRepository;
    private final BranchRepository branchRepository;
    private final ApplicationEventPublisher publisher;

    public PlanTargetService(final PlanTargetRepository planTargetRepository,
            final BranchRepository branchRepository, final ApplicationEventPublisher publisher) {
        this.planTargetRepository = planTargetRepository;
        this.branchRepository = branchRepository;
        this.publisher = publisher;
    }

    public List<PlanTargetDTO> findAll() {
        final List<PlanTarget> planTargets = planTargetRepository.findAll(Sort.by("id"));
        return planTargets.stream()
                .map(planTarget -> mapToDTO(planTarget, new PlanTargetDTO()))
                .toList();
    }

    public PlanTargetDTO get(final Long id) {
        return planTargetRepository.findById(id)
                .map(planTarget -> mapToDTO(planTarget, new PlanTargetDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanTargetDTO planTargetDTO) {
        final PlanTarget planTarget = new PlanTarget();
        mapToEntity(planTargetDTO, planTarget);
        return planTargetRepository.save(planTarget).getId();
    }

    public void update(final Long id, final PlanTargetDTO planTargetDTO) {
        final PlanTarget planTarget = planTargetRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planTargetDTO, planTarget);
        planTargetRepository.save(planTarget);
    }

    public void delete(final Long id) {
        final PlanTarget planTarget = planTargetRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePlanTarget(id));
        planTargetRepository.delete(planTarget);
    }

    private PlanTargetDTO mapToDTO(final PlanTarget planTarget, final PlanTargetDTO planTargetDTO) {
        planTargetDTO.setId(planTarget.getId());
        planTargetDTO.setTargetDescription(planTarget.getTargetDescription());
        planTargetDTO.setTargetValue(planTarget.getTargetValue());
        planTargetDTO.setCritical(planTarget.getCritical());
        planTargetDTO.setCreatedAt(planTarget.getCreatedAt());
        planTargetDTO.setUpdatedAt(planTarget.getUpdatedAt());
        planTargetDTO.setCreatedBy(planTarget.getCreatedBy());
        planTargetDTO.setUpdatedBy(planTarget.getUpdatedBy());
        planTargetDTO.setStatus(planTarget.getStatus());

        if (planTarget.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(planTarget.getBranch().getId());
            branchCopy.setCode(planTarget.getBranch().getCode());
            branchCopy.setName(planTarget.getBranch().getName());
            branchCopy.setStatus(planTarget.getBranch().getStatus());
            branchCopy.setCreatedAt(planTarget.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(planTarget.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(planTarget.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(planTarget.getBranch().getUpdatedBy());

            // Loại bỏ các quan hệ con để tránh vòng lặp hoặc dữ liệu thừa
            branchCopy.setFactory(null);
            branchCopy.setBranchDayOffs(null);
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchForms(null);
            branchCopy.setBranchPlanTargets(null);

            planTargetDTO.setBranch(branchCopy);
        } else {
            planTargetDTO.setBranch(null);
        }

        return planTargetDTO;
    }


    private PlanTarget mapToEntity(final PlanTargetDTO planTargetDTO, final PlanTarget planTarget) {
        planTarget.setTargetDescription(planTargetDTO.getTargetDescription());
        planTarget.setTargetValue(planTargetDTO.getTargetValue());
        planTarget.setCritical(planTargetDTO.getCritical());
        planTarget.setCreatedAt(planTargetDTO.getCreatedAt());
        planTarget.setUpdatedAt(planTargetDTO.getUpdatedAt());
        planTarget.setCreatedBy(planTargetDTO.getCreatedBy());
        planTarget.setUpdatedBy(planTargetDTO.getUpdatedBy());
        planTarget.setStatus(planTargetDTO.getStatus());
        final Branch branch = planTargetDTO.getBranch() == null ? null : branchRepository.findById(planTargetDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        planTarget.setBranch(branch);
        return planTarget;
    }

    @EventListener(BeforeDeleteBranch.class)
    public void on(final BeforeDeleteBranch event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanTarget branchPlanTarget = planTargetRepository.findFirstByBranchId(event.getId());
        if (branchPlanTarget != null) {
            referencedException.setKey("branch.planTarget.branch.referenced");
            referencedException.addParam(branchPlanTarget.getId());
            throw referencedException;
        }
    }

}
