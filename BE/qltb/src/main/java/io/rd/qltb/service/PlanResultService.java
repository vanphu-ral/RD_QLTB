package io.rd.qltb.service;

import io.rd.qltb.domain.PlanResult;
import io.rd.qltb.events.BeforeDeletePlanResult;
import io.rd.qltb.model.PlanResultDTO;
import io.rd.qltb.repos.PlanResultRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanResultService {

    private final PlanResultRepository planResultRepository;
    private final ApplicationEventPublisher publisher;

    public PlanResultService(final PlanResultRepository planResultRepository,
            final ApplicationEventPublisher publisher) {
        this.planResultRepository = planResultRepository;
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
        planResultDTO.setPlanResultDetailId(planResult.getPlanResultDetailId());
        planResultDTO.setNote(planResult.getNote());
        planResultDTO.setDateTest(planResult.getDateTest());
        planResultDTO.setUserTest(planResult.getUserTest());
        planResultDTO.setCreatedAt(planResult.getCreatedAt());
        planResultDTO.setUpdatedAt(planResult.getUpdatedAt());
        planResultDTO.setCreatedBy(planResult.getCreatedBy());
        planResultDTO.setUpdatedBy(planResult.getUpdatedBy());
        planResultDTO.setStatus(planResult.getStatus());
        planResultDTO.setStatusRepair(planResult.getStatusRepair());
        return planResultDTO;
    }

    private PlanResult mapToEntity(final PlanResultDTO planResultDTO, final PlanResult planResult) {
        planResult.setCode(planResultDTO.getCode());
        planResult.setPlanResultDetailId(planResultDTO.getPlanResultDetailId());
        planResult.setNote(planResultDTO.getNote());
        planResult.setDateTest(planResultDTO.getDateTest());
        planResult.setUserTest(planResultDTO.getUserTest());
        planResult.setCreatedAt(planResultDTO.getCreatedAt());
        planResult.setUpdatedAt(planResultDTO.getUpdatedAt());
        planResult.setCreatedBy(planResultDTO.getCreatedBy());
        planResult.setUpdatedBy(planResultDTO.getUpdatedBy());
        planResult.setStatus(planResultDTO.getStatus());
        planResult.setStatusRepair(planResultDTO.getStatusRepair());
        return planResult;
    }

}
