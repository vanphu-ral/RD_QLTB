package io.qltb.qltb.service;

import io.qltb.qltb.domain.PlanResult;
import io.qltb.qltb.domain.PlanResultDetail;
import io.qltb.qltb.events.BeforeDeletePlanResult;
import io.qltb.qltb.model.PlanResultDetailDTO;
import io.qltb.qltb.repos.PlanResultDetailRepository;
import io.qltb.qltb.repos.PlanResultRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanResultDetailService {

    private final PlanResultDetailRepository planResultDetailRepository;
    private final PlanResultRepository planResultRepository;

    public PlanResultDetailService(final PlanResultDetailRepository planResultDetailRepository,
            final PlanResultRepository planResultRepository) {
        this.planResultDetailRepository = planResultDetailRepository;
        this.planResultRepository = planResultRepository;
    }

    public List<PlanResultDetailDTO> findAll() {
        final List<PlanResultDetail> planResultDetails = planResultDetailRepository.findAll(Sort.by("id"));
        return planResultDetails.stream()
                .map(planResultDetail -> mapToDTO(planResultDetail, new PlanResultDetailDTO()))
                .toList();
    }

    public PlanResultDetailDTO get(final Long id) {
        return planResultDetailRepository.findById(id)
                .map(planResultDetail -> mapToDTO(planResultDetail, new PlanResultDetailDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanResultDetailDTO planResultDetailDTO) {
        final PlanResultDetail planResultDetail = new PlanResultDetail();
        mapToEntity(planResultDetailDTO, planResultDetail);
        return planResultDetailRepository.save(planResultDetail).getId();
    }

    public void update(final Long id, final PlanResultDetailDTO planResultDetailDTO) {
        final PlanResultDetail planResultDetail = planResultDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planResultDetailDTO, planResultDetail);
        planResultDetailRepository.save(planResultDetail);
    }

    public void delete(final Long id) {
        final PlanResultDetail planResultDetail = planResultDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        planResultDetailRepository.delete(planResultDetail);
    }

    private PlanResultDetailDTO mapToDTO(final PlanResultDetail planResultDetail,
                                         final PlanResultDetailDTO planResultDetailDTO) {
        planResultDetailDTO.setId(planResultDetail.getId());
        planResultDetailDTO.setCriticalCode(planResultDetail.getCriticalCode());
        planResultDetailDTO.setCriticalName(planResultDetail.getCriticalName());
        planResultDetailDTO.setFrequency(planResultDetail.getFrequency());
        planResultDetailDTO.setType(planResultDetail.getType());
        planResultDetailDTO.setResult(planResultDetail.getResult());
        planResultDetailDTO.setNote(planResultDetail.getNote());
        planResultDetailDTO.setUnit(planResultDetail.getUnit());
        planResultDetailDTO.setMin(planResultDetail.getMin());
        planResultDetailDTO.setMax(planResultDetail.getMax());
        planResultDetailDTO.setCreatedAt(planResultDetail.getCreatedAt());
        planResultDetailDTO.setUpdatedAt(planResultDetail.getUpdatedAt());
        planResultDetailDTO.setCreatedBy(planResultDetail.getCreatedBy());
        planResultDetailDTO.setUpdatedBy(planResultDetail.getUpdatedBy());
        planResultDetailDTO.setStatus(planResultDetail.getStatus());

        if (planResultDetail.getPlanResult() != null) {
            PlanResult planResultCopy = new PlanResult();
            planResultCopy.setId(planResultDetail.getPlanResult().getId());
            planResultCopy.setCode(planResultDetail.getPlanResult().getCode());
            planResultCopy.setStatus(planResultDetail.getPlanResult().getStatus());
            planResultCopy.setCreatedAt(planResultDetail.getPlanResult().getCreatedAt());
            planResultCopy.setUpdatedAt(planResultDetail.getPlanResult().getUpdatedAt());
            planResultCopy.setCreatedBy(planResultDetail.getPlanResult().getCreatedBy());
            planResultCopy.setUpdatedBy(planResultDetail.getPlanResult().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp hoặc dữ liệu thừa
            planResultCopy.setPlanResultDetail(null);
            planResultCopy.setPlanResultSupplyReplacements(null);
            planResultCopy.setPlanResultErrorReports(null);
            planResultCopy.setPlanResultAcceptances(null);
            planResultCopy.setPlanResultPlanResultDetails(null);

            planResultDetailDTO.setPlanResult(planResultCopy);
        } else {
            planResultDetailDTO.setPlanResult(null);
        }

        return planResultDetailDTO;
    }


    private PlanResultDetail mapToEntity(final PlanResultDetailDTO planResultDetailDTO,
            final PlanResultDetail planResultDetail) {
        planResultDetail.setCriticalCode(planResultDetailDTO.getCriticalCode());
        planResultDetail.setCriticalName(planResultDetailDTO.getCriticalName());
        planResultDetail.setFrequency(planResultDetailDTO.getFrequency());
        planResultDetail.setType(planResultDetailDTO.getType());
        planResultDetail.setResult(planResultDetailDTO.getResult());
        planResultDetail.setNote(planResultDetailDTO.getNote());
        planResultDetail.setUnit(planResultDetailDTO.getUnit());
        planResultDetail.setMin(planResultDetailDTO.getMin());
        planResultDetail.setMax(planResultDetailDTO.getMax());
        planResultDetail.setCreatedAt(planResultDetailDTO.getCreatedAt());
        planResultDetail.setUpdatedAt(planResultDetailDTO.getUpdatedAt());
        planResultDetail.setCreatedBy(planResultDetailDTO.getCreatedBy());
        planResultDetail.setUpdatedBy(planResultDetailDTO.getUpdatedBy());
        planResultDetail.setStatus(planResultDetailDTO.getStatus());
        final PlanResult planResult = planResultDetailDTO.getPlanResult() == null ? null : planResultRepository.findById(planResultDetailDTO.getPlanResult().getId())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        planResultDetail.setPlanResult(planResult);
        return planResultDetail;
    }

    public boolean criticalCodeExists(final String criticalCode) {
        return planResultDetailRepository.existsByCriticalCodeIgnoreCase(criticalCode);
    }

    @EventListener(BeforeDeletePlanResult.class)
    public void on(final BeforeDeletePlanResult event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanResultDetail planResultPlanResultDetail = planResultDetailRepository.findFirstByPlanResultId(event.getId());
        if (planResultPlanResultDetail != null) {
            referencedException.setKey("planResult.planResultDetail.planResult.referenced");
            referencedException.addParam(planResultPlanResultDetail.getId());
            throw referencedException;
        }
    }

}
