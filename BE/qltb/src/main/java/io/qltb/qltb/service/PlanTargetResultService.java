package io.qltb.qltb.service;

import io.qltb.qltb.domain.PlanTarget;
import io.qltb.qltb.domain.PlanTargetResult;
import io.qltb.qltb.events.BeforeDeletePlanTarget;
import io.qltb.qltb.model.PlanTargetResultDTO;
import io.qltb.qltb.repos.PlanTargetRepository;
import io.qltb.qltb.repos.PlanTargetResultRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanTargetResultService {

    private final PlanTargetResultRepository planTargetResultRepository;
    private final PlanTargetRepository planTargetRepository;

    public PlanTargetResultService(final PlanTargetResultRepository planTargetResultRepository,
            final PlanTargetRepository planTargetRepository) {
        this.planTargetResultRepository = planTargetResultRepository;
        this.planTargetRepository = planTargetRepository;
    }

    public List<PlanTargetResultDTO> findAll() {
        final List<PlanTargetResult> planTargetResults = planTargetResultRepository.findAll(Sort.by("id"));
        return planTargetResults.stream()
                .map(planTargetResult -> mapToDTO(planTargetResult, new PlanTargetResultDTO()))
                .toList();
    }

    public PlanTargetResultDTO get(final Long id) {
        return planTargetResultRepository.findById(id)
                .map(planTargetResult -> mapToDTO(planTargetResult, new PlanTargetResultDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanTargetResultDTO planTargetResultDTO) {
        final PlanTargetResult planTargetResult = new PlanTargetResult();
        mapToEntity(planTargetResultDTO, planTargetResult);
        return planTargetResultRepository.save(planTargetResult).getId();
    }

    public void update(final Long id, final PlanTargetResultDTO planTargetResultDTO) {
        final PlanTargetResult planTargetResult = planTargetResultRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planTargetResultDTO, planTargetResult);
        planTargetResultRepository.save(planTargetResult);
    }

    public void delete(final Long id) {
        final PlanTargetResult planTargetResult = planTargetResultRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        planTargetResultRepository.delete(planTargetResult);
    }

    private PlanTargetResultDTO mapToDTO(final PlanTargetResult planTargetResult,
                                         final PlanTargetResultDTO planTargetResultDTO) {
        planTargetResultDTO.setId(planTargetResult.getId());
        planTargetResultDTO.setResult(planTargetResult.getResult());
        planTargetResultDTO.setNote(planTargetResult.getNote());
        planTargetResultDTO.setCreatedAt(planTargetResult.getCreatedAt());
        planTargetResultDTO.setUpdatedAt(planTargetResult.getUpdatedAt());
        planTargetResultDTO.setCreatedBy(planTargetResult.getCreatedBy());
        planTargetResultDTO.setUpdatedBy(planTargetResult.getUpdatedBy());
        planTargetResultDTO.setStatus(planTargetResult.getStatus());

        if (planTargetResult.getPlanTargetDevice() != null) {
            PlanTarget deviceCopy = new PlanTarget();
            deviceCopy.setId(planTargetResult.getPlanTargetDevice().getId());
            deviceCopy.setTargetDescription(planTargetResult.getPlanTargetDevice().getTargetDescription());
            deviceCopy.setTargetValue(planTargetResult.getPlanTargetDevice().getTargetValue());
            deviceCopy.setCritical(planTargetResult.getPlanTargetDevice().getCritical());
            deviceCopy.setStatus(planTargetResult.getPlanTargetDevice().getStatus());
            deviceCopy.setCreatedAt(planTargetResult.getPlanTargetDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(planTargetResult.getPlanTargetDevice().getUpdatedAt());
            deviceCopy.setCreatedBy(planTargetResult.getPlanTargetDevice().getCreatedBy());
            deviceCopy.setUpdatedBy(planTargetResult.getPlanTargetDevice().getUpdatedBy());

            // Loại bỏ các quan hệ con để tránh vòng lặp hoặc dữ liệu thừa
            deviceCopy.setPlanTargetDevicePlanTargetResults(null);
            deviceCopy.setBranch(null);

            planTargetResultDTO.setPlanTargetDevice(deviceCopy);
        } else {
            planTargetResultDTO.setPlanTargetDevice(null);
        }

        return planTargetResultDTO;
    }


    private PlanTargetResult mapToEntity(final PlanTargetResultDTO planTargetResultDTO,
            final PlanTargetResult planTargetResult) {
        planTargetResult.setResult(planTargetResultDTO.getResult());
        planTargetResult.setNote(planTargetResultDTO.getNote());
        planTargetResult.setCreatedAt(planTargetResultDTO.getCreatedAt());
        planTargetResult.setUpdatedAt(planTargetResultDTO.getUpdatedAt());
        planTargetResult.setCreatedBy(planTargetResultDTO.getCreatedBy());
        planTargetResult.setUpdatedBy(planTargetResultDTO.getUpdatedBy());
        planTargetResult.setStatus(planTargetResultDTO.getStatus());
        final PlanTarget planTargetDevice = planTargetResultDTO.getPlanTargetDevice() == null ? null : planTargetRepository.findById(planTargetResultDTO.getPlanTargetDevice().getId())
                .orElseThrow(() -> new NotFoundException("planTargetDevice not found"));
        planTargetResult.setPlanTargetDevice(planTargetDevice);
        return planTargetResult;
    }

    @EventListener(BeforeDeletePlanTarget.class)
    public void on(final BeforeDeletePlanTarget event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanTargetResult planTargetDevicePlanTargetResult = planTargetResultRepository.findFirstByPlanTargetDeviceId(event.getId());
        if (planTargetDevicePlanTargetResult != null) {
            referencedException.setKey("planTarget.planTargetResult.planTargetDevice.referenced");
            referencedException.addParam(planTargetDevicePlanTargetResult.getId());
            throw referencedException;
        }
    }

}
