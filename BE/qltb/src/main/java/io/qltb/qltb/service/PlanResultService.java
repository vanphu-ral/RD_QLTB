package io.qltb.qltb.service;

import io.qltb.qltb.domain.PlanDetail;
import io.qltb.qltb.domain.PlanResult;
import io.qltb.qltb.events.BeforeDeletePlanDetail;
import io.qltb.qltb.events.BeforeDeletePlanResult;
import io.qltb.qltb.model.PlanResultDTO;
import io.qltb.qltb.repos.PlanDetailRepository;
import io.qltb.qltb.repos.PlanResultRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanResultService {

    private final PlanResultRepository planResultRepository;
    private final PlanDetailRepository planDetailRepository;
    private final ApplicationEventPublisher publisher;

    public PlanResultService(final PlanResultRepository planResultRepository,
            final PlanDetailRepository planDetailRepository,
            final ApplicationEventPublisher publisher) {
        this.planResultRepository = planResultRepository;
        this.planDetailRepository = planDetailRepository;
        this.publisher = publisher;
    }

    public List<PlanResultDTO> findAll() {
        final List<PlanResult> planResults = planResultRepository.findAll(Sort.by("id"));
        return planResults.stream()
                .map(planResult -> mapToDTO(planResult, new PlanResultDTO()))
                .toList();
    }

    public PlanResultDTO get(final Long id) {
        return planResultRepository.findById(id)
                .map(planResult -> mapToDTO(planResult, new PlanResultDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanResultDTO planResultDTO) {
        final PlanResult planResult = new PlanResult();
        mapToEntity(planResultDTO, planResult);
        return planResultRepository.save(planResult).getId();
    }

    public void update(final Long id, final PlanResultDTO planResultDTO) {
        final PlanResult planResult = planResultRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planResultDTO, planResult);
        planResultRepository.save(planResult);
    }

    public void delete(final Long id) {
        final PlanResult planResult = planResultRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePlanResult(id));
        planResultRepository.delete(planResult);
    }

    private PlanResultDTO mapToDTO(final PlanResult planResult, final PlanResultDTO planResultDTO) {
        planResultDTO.setId(planResult.getId());
        planResultDTO.setCode(planResult.getCode());
        planResultDTO.setNote(planResult.getNote());
        planResultDTO.setCreatedAt(planResult.getCreatedAt());
        planResultDTO.setUpdatedAt(planResult.getUpdatedAt());
        planResultDTO.setCreatedBy(planResult.getCreatedBy());
        planResultDTO.setUpdatedBy(planResult.getUpdatedBy());
        planResultDTO.setStatus(planResult.getStatus());
        planResultDTO.setStatusRepair(planResult.getStatusRepair());
        planResultDTO.setPlanResultDetail(planResult.getPlanResultDetail() == null ? null : planResult.getPlanResultDetail());
        return planResultDTO;
    }

    private PlanResult mapToEntity(final PlanResultDTO planResultDTO, final PlanResult planResult) {
        planResult.setCode(planResultDTO.getCode());
        planResult.setNote(planResultDTO.getNote());
        planResult.setCreatedAt(planResultDTO.getCreatedAt());
        planResult.setUpdatedAt(planResultDTO.getUpdatedAt());
        planResult.setCreatedBy(planResultDTO.getCreatedBy());
        planResult.setUpdatedBy(planResultDTO.getUpdatedBy());
        planResult.setStatus(planResultDTO.getStatus());
        planResult.setStatusRepair(planResultDTO.getStatusRepair());
        final PlanDetail planResultDetail = planResultDTO.getPlanResultDetail() == null ? null : planDetailRepository.findById(planResultDTO.getPlanResultDetail().getId())
                .orElseThrow(() -> new NotFoundException("planResultDetail not found"));
        planResult.setPlanResultDetail(planResultDetail);
        return planResult;
    }

    @EventListener(BeforeDeletePlanDetail.class)
    public void on(final BeforeDeletePlanDetail event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanResult planResultDetailPlanResult = planResultRepository.findFirstByPlanResultDetailId(event.getId());
        if (planResultDetailPlanResult != null) {
            referencedException.setKey("planDetail.planResult.planResultDetail.referenced");
            referencedException.addParam(planResultDetailPlanResult.getId());
            throw referencedException;
        }
    }

}
