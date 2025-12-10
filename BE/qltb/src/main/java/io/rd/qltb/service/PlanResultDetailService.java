package io.rd.qltb.service;

import io.rd.qltb.domain.PlanResult;
import io.rd.qltb.domain.PlanResultDetail;
import io.rd.qltb.events.BeforeDeletePlanResult;
import io.rd.qltb.model.PlanResultDTO;
import io.rd.qltb.model.PlanResultDetailDTO;
import io.rd.qltb.repos.PlanResultDetailRepository;
import io.rd.qltb.repos.PlanResultRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
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
        final List<PlanResultDetail> planResultDetails = planResultDetailRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
    public List<PlanResultDetailDTO> findAllByPlanResultId(Long planDetailId) {
        List<PlanResultDetail> planResultDetails = planResultDetailRepository.findByPlanResultId(planDetailId);
        return planResultDetails.stream()
                .map(planResultDetail -> mapToDTO(planResultDetail, new PlanResultDetailDTO()))
                .toList();
    }
    public PlanResultDetailDTO mapToDTO(final PlanResultDetail planResultDetail,
                                         final PlanResultDetailDTO dto) {
        dto.setId(planResultDetail.getId());
        dto.setCriticalCode(planResultDetail.getCriticalCode());
        dto.setCriticalGroup(planResultDetail.getCriticalGroup());
        dto.setCriticalName(planResultDetail.getCriticalName());
        dto.setFrequency(planResultDetail.getFrequency());
        dto.setInspectionSession(planResultDetail.getInspectionSession());
        dto.setType(planResultDetail.getType());
        dto.setResult(planResultDetail.getResult());
        dto.setNote(planResultDetail.getNote());
        dto.setUnit(planResultDetail.getUnit());
        dto.setMin(planResultDetail.getMin());
        dto.setMax(planResultDetail.getMax());
        dto.setFile(planResultDetail.getFile());
        dto.setCommittee(planResultDetail.getCommittee());
        dto.setComment(planResultDetail.getComment());
        dto.setCreatedAt(planResultDetail.getCreatedAt());
        dto.setUpdatedAt(planResultDetail.getUpdatedAt());
        dto.setCreatedBy(planResultDetail.getCreatedBy());
        dto.setUpdatedBy(planResultDetail.getUpdatedBy());
        dto.setStatus(planResultDetail.getStatus());

        // Sao chép PlanResult có kiểm soát
        if (planResultDetail.getPlanResult() != null) {
            PlanResult planResultCopy = new PlanResult();
            planResultCopy.setId(planResultDetail.getPlanResult().getId());
            planResultCopy.setDateTest(planResultDetail.getPlanResult().getDateTest());
            planResultCopy.setUserTest(planResultDetail.getPlanResult().getUserTest());
            planResultCopy.setNote(planResultDetail.getPlanResult().getNote());
            planResultCopy.setStatus(planResultDetail.getPlanResult().getStatus());
            planResultCopy.setStatusRepair(planResultDetail.getPlanResult().getStatusRepair());
            planResultCopy.setCreatedAt(planResultDetail.getPlanResult().getCreatedAt());
            planResultCopy.setUpdatedAt(planResultDetail.getPlanResult().getUpdatedAt());
            planResultCopy.setCreatedBy(planResultDetail.getPlanResult().getCreatedBy());
            planResultCopy.setUpdatedBy(planResultDetail.getPlanResult().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp
            planResultCopy.setPlanResultPlanResultDetails(null);
            planResultCopy.setPlanResultErrorReports(null);
            planResultCopy.setPlanResultAcceptances(null);
            planResultCopy.setPlanResultSupplyReplacements(null);

            dto.setPlanResult(planResultCopy);
        } else {
            dto.setPlanResult(null);
        }

        return dto;
    }


    public PlanResultDetail mapToEntity(final PlanResultDetailDTO planResultDetailDTO,
            final PlanResultDetail planResultDetail) {
        planResultDetail.setCriticalCode(planResultDetailDTO.getCriticalCode());
        planResultDetail.setCriticalGroup(planResultDetailDTO.getCriticalGroup());
        planResultDetail.setCriticalName(planResultDetailDTO.getCriticalName());
        planResultDetail.setFrequency(planResultDetailDTO.getFrequency());
        planResultDetail.setInspectionSession(planResultDetailDTO.getInspectionSession());
        planResultDetail.setType(planResultDetailDTO.getType());
        planResultDetail.setResult(planResultDetailDTO.getResult());
        planResultDetail.setNote(planResultDetailDTO.getNote());
        planResultDetail.setUnit(planResultDetailDTO.getUnit());
        planResultDetail.setMin(planResultDetailDTO.getMin());
        planResultDetail.setMax(planResultDetailDTO.getMax());
        planResultDetail.setFile(planResultDetailDTO.getFile());
        planResultDetail.setCommittee(planResultDetailDTO.getCommittee());
        planResultDetail.setComment(planResultDetailDTO.getComment());
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
