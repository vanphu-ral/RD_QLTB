package io.rd.qltb.service;

import io.rd.qltb.domain.PlanTarget;
import io.rd.qltb.domain.PlanTargetResult;
import io.rd.qltb.events.BeforeDeletePlanTarget;
import io.rd.qltb.model.PlanTargetResultDTO;
import io.rd.qltb.repos.PlanTargetRepository;
import io.rd.qltb.repos.PlanTargetResultRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
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
        final List<PlanTargetResult> planTargetResults = planTargetResultRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
                                         final PlanTargetResultDTO dto) {
        dto.setId(planTargetResult.getId());
        dto.setResult(planTargetResult.getResult());
        dto.setNote(planTargetResult.getNote());
        dto.setCreatedAt(planTargetResult.getCreatedAt());
        dto.setUpdatedAt(planTargetResult.getUpdatedAt());
        dto.setCreatedBy(planTargetResult.getCreatedBy());
        dto.setUpdatedBy(planTargetResult.getUpdatedBy());
        dto.setStatus(planTargetResult.getStatus());

        // Sao chép PlanTarget (planTargetDevice) có kiểm soát
        if (planTargetResult.getPlanTargetDevice() != null) {
            PlanTarget targetCopy = new PlanTarget();
            targetCopy.setId(planTargetResult.getPlanTargetDevice().getId());
            targetCopy.setBranchId(planTargetResult.getPlanTargetDevice().getBranchId());
            targetCopy.setTargetDescription(planTargetResult.getPlanTargetDevice().getTargetDescription());
            targetCopy.setTargetValue(planTargetResult.getPlanTargetDevice().getTargetValue());
            targetCopy.setCritical(planTargetResult.getPlanTargetDevice().getCritical());
            targetCopy.setCreatedAt(planTargetResult.getPlanTargetDevice().getCreatedAt());
            targetCopy.setUpdatedAt(planTargetResult.getPlanTargetDevice().getUpdatedAt());
            targetCopy.setCreatedBy(planTargetResult.getPlanTargetDevice().getCreatedBy());
            targetCopy.setUpdatedBy(planTargetResult.getPlanTargetDevice().getUpdatedBy());
            targetCopy.setStatus(planTargetResult.getPlanTargetDevice().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            targetCopy.setPlanTargetDevicePlanTargetResults(null);

            dto.setPlanTargetDevice(targetCopy);
        } else {
            dto.setPlanTargetDevice(null);
        }

        return dto;
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
