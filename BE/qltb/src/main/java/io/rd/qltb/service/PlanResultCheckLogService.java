package io.rd.qltb.service;

import io.rd.qltb.domain.PlanResult;
import io.rd.qltb.domain.PlanResultCheckLog;
import io.rd.qltb.model.PlanResultCheckLogDTO;
import io.rd.qltb.repos.PlanResultCheckLogRepository;
import io.rd.qltb.repos.PlanResultRepository;
import io.rd.qltb.util.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanResultCheckLogService {
    private final PlanResultCheckLogRepository planResultCheckLogRepository;
    private final PlanResultRepository planResultRepository;

    public PlanResultCheckLogService(
            final PlanResultCheckLogRepository planResultCheckLogRepository,
            final PlanResultRepository planResultRepository) {
        this.planResultCheckLogRepository = planResultCheckLogRepository;
        this.planResultRepository = planResultRepository;
    }

    public List<PlanResultCheckLogDTO> findAll() {
        final List<PlanResultCheckLog> planResultCheckLogs = planResultCheckLogRepository.findAll(Sort.by("id"));
        return planResultCheckLogs.stream()
                .map(planResultCheckLog -> mapToDTO(planResultCheckLog, new PlanResultCheckLogDTO()))
                .toList();
    }

    public PlanResultCheckLogDTO get(final Long id) {
        return planResultCheckLogRepository.findById(id)
                .map(planResultCheckLog -> mapToDTO(planResultCheckLog, new PlanResultCheckLogDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanResultCheckLogDTO planResultCheckLogDTO) {
        final PlanResultCheckLog planResultCheckLog = new PlanResultCheckLog();
        mapToEntity(planResultCheckLogDTO, planResultCheckLog);
        return planResultCheckLogRepository.save(planResultCheckLog).getId();
    }

    public void update(final Long id, final PlanResultCheckLogDTO planResultCheckLogDTO) {
        final PlanResultCheckLog planResultCheckLog = planResultCheckLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planResultCheckLogDTO, planResultCheckLog);
        planResultCheckLogRepository.save(planResultCheckLog);
    }

    public void delete(final Long id) {
        final PlanResultCheckLog planResultCheckLog = planResultCheckLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        planResultCheckLogRepository.delete(planResultCheckLog);
    }

    private PlanResultCheckLogDTO mapToDTO(final PlanResultCheckLog planResultCheckLog,
                                           final PlanResultCheckLogDTO planResultCheckLogDTO) {
        planResultCheckLogDTO.setId(planResultCheckLog.getId());
        planResultCheckLogDTO.setInspection(planResultCheckLog.getInspection());
        planResultCheckLogDTO.setContent(planResultCheckLog.getContent());
        planResultCheckLogDTO.setCreatedAt(planResultCheckLog.getCreatedAt());
        planResultCheckLogDTO.setUpdatedAt(planResultCheckLog.getUpdatedAt());
        planResultCheckLogDTO.setCreatedBy(planResultCheckLog.getCreatedBy());
        planResultCheckLogDTO.setUpdatedBy(planResultCheckLog.getUpdatedBy());
        planResultCheckLogDTO.setStatus(planResultCheckLog.getStatus());
        if (planResultCheckLog.getPlanResult() != null) {
            PlanResult planResultCopy = new PlanResult();
            planResultCopy.setId(planResultCheckLog.getPlanResult().getId());
            planResultCopy.setDateTest(planResultCheckLog.getPlanResult().getDateTest());
            planResultCopy.setUserTest(planResultCheckLog.getPlanResult().getUserTest());
            planResultCopy.setStatus(planResultCheckLog.getPlanResult().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            planResultCopy.setPlanResultPlanResultDetails(null);
            planResultCopy.setPlanResultErrorReports(null);
            planResultCopy.setPlanResultAcceptances(null);
            planResultCopy.setPlanResultSupplyReplacements(null);

            planResultCheckLogDTO.setPlanResult(planResultCopy);
        } else {
            planResultCheckLogDTO.setPlanResult(null);
        }
        return planResultCheckLogDTO;
    }

    private PlanResultCheckLog mapToEntity(final PlanResultCheckLogDTO planResultCheckLogDTO,
                                           final PlanResultCheckLog planResultCheckLog) {
        planResultCheckLog.setInspection(planResultCheckLogDTO.getInspection());
        planResultCheckLog.setContent(planResultCheckLogDTO.getContent());
        planResultCheckLog.setCreatedAt(planResultCheckLogDTO.getCreatedAt());
        planResultCheckLog.setUpdatedAt(planResultCheckLogDTO.getUpdatedAt());
        planResultCheckLog.setCreatedBy(planResultCheckLogDTO.getCreatedBy());
        planResultCheckLog.setUpdatedBy(planResultCheckLogDTO.getUpdatedBy());
        planResultCheckLog.setStatus(planResultCheckLogDTO.getStatus());

        final PlanResult planResult = planResultCheckLogDTO.getPlanResult() == null ? null : planResultRepository.findById(planResultCheckLogDTO.getPlanResult().getId())
                .orElseThrow(() -> new NotFoundException("planResult not found"));
        planResultCheckLog.setPlanResult(planResult);
        return planResultCheckLog;
    }
}
