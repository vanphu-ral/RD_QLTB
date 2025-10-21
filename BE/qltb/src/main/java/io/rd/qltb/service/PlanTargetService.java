package io.rd.qltb.service;

import io.rd.qltb.domain.PlanTarget;
import io.rd.qltb.events.BeforeDeletePlanTarget;
import io.rd.qltb.model.PlanTargetDTO;
import io.rd.qltb.repos.PlanTargetRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanTargetService {

    private final PlanTargetRepository planTargetRepository;
    private final ApplicationEventPublisher publisher;

    public PlanTargetService(final PlanTargetRepository planTargetRepository,
            final ApplicationEventPublisher publisher) {
        this.planTargetRepository = planTargetRepository;
        this.publisher = publisher;
    }

    public List<PlanTargetDTO> findAll() {
        final List<PlanTarget> planTargets = planTargetRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
        planTargetDTO.setBranchId(planTarget.getBranchId());
        planTargetDTO.setTargetDescription(planTarget.getTargetDescription());
        planTargetDTO.setTargetValue(planTarget.getTargetValue());
        planTargetDTO.setCritical(planTarget.getCritical());
        planTargetDTO.setCreatedAt(planTarget.getCreatedAt());
        planTargetDTO.setUpdatedAt(planTarget.getUpdatedAt());
        planTargetDTO.setCreatedBy(planTarget.getCreatedBy());
        planTargetDTO.setUpdatedBy(planTarget.getUpdatedBy());
        planTargetDTO.setStatus(planTarget.getStatus());
        return planTargetDTO;
    }

    private PlanTarget mapToEntity(final PlanTargetDTO planTargetDTO, final PlanTarget planTarget) {
        planTarget.setBranchId(planTargetDTO.getBranchId());
        planTarget.setTargetDescription(planTargetDTO.getTargetDescription());
        planTarget.setTargetValue(planTargetDTO.getTargetValue());
        planTarget.setCritical(planTargetDTO.getCritical());
        planTarget.setCreatedAt(planTargetDTO.getCreatedAt());
        planTarget.setUpdatedAt(planTargetDTO.getUpdatedAt());
        planTarget.setCreatedBy(planTargetDTO.getCreatedBy());
        planTarget.setUpdatedBy(planTargetDTO.getUpdatedBy());
        planTarget.setStatus(planTargetDTO.getStatus());
        return planTarget;
    }

}
