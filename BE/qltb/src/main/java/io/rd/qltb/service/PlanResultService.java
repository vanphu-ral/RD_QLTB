package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeletePlanResult;
import io.rd.qltb.model.PlanCheckDTO;
import io.rd.qltb.model.PlanResultDTO;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;

import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanResultService {

    private final PlanResultRepository planResultRepository;
    private final ApplicationEventPublisher publisher;
    private final PlanResultDetailService planResultDetailService;

    private final ErrorReportService errorReportService;
    private final PlanResultDetailRepository planResultDetailRepository;
    private final SupplyReplacementService supplyReplacementService;
    private final SupplyReplacementRepository supplyReplacementRepository;
    private final ErrorReportRepository errorReportRepository;
    private final PlanDetailRepository planDetailRepository;

    public PlanResultService(final PlanResultRepository planResultRepository,
                             final ApplicationEventPublisher publisher, PlanResultDetailService planResultDetailService, ErrorReportService errorReportService, PlanResultDetailRepository planResultDetailRepository, SupplyReplacementService supplyReplacementService, SupplyReplacementRepository supplyReplacementRepository, ErrorReportRepository errorReportRepository, PlanDetailRepository planDetailRepository) {
        this.planResultRepository = planResultRepository;
        this.publisher = publisher;
        this.planResultDetailService = planResultDetailService;
        this.errorReportService = errorReportService;
        this.planResultDetailRepository = planResultDetailRepository;
        this.supplyReplacementService = supplyReplacementService;
        this.supplyReplacementRepository = supplyReplacementRepository;
        this.errorReportRepository = errorReportRepository;
        this.planDetailRepository = planDetailRepository;
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
    public  void createUpdate(PlanCheckDTO planCheckDTO, String userName){
        PlanResult planResult = planResultRepository.findById(planCheckDTO.getPlanResultDTO().getId()).orElse(new PlanResult());
        if (planCheckDTO.getPlanResultDetailDTOS() != null && planCheckDTO.getPlanResultDetailDTOS().size() > 0) {
            planCheckDTO.getPlanResultDetailDTOS().forEach(item -> {
//                if(item.getId() != null){
//                    PlanResultDetail existingDetail = planResultDetailService.mapToEntity(item, planResultDetailRepository.findById(item.getId()).orElse(new PlanResultDetail()));
//                }else{
                item.setPlanResult(planResult);
                item.setCreatedBy(userName);
                item.setCreatedAt(java.time.LocalDateTime.now());
                item.setUpdatedAt(java.time.LocalDateTime.now());
                PlanResultDetail planResultDetail = planResultDetailService.mapToEntity(item, new PlanResultDetail());
                planResultDetailRepository.save(planResultDetail);
//                }
            });
        }
        if(planCheckDTO.getSupplyReplacementDTOS() != null && planCheckDTO.getSupplyReplacementDTOS().size() > 0){
            planCheckDTO.getSupplyReplacementDTOS().forEach(item -> {
                item.setCreatedBy(userName);
                item.setCreatedAt(java.time.LocalDateTime.now());
                item.setUpdatedAt(java.time.LocalDateTime.now());
                item.setPlanResult(planResult);
                SupplyReplacement supplyReplacement = supplyReplacementService.mapToEntity(item, new SupplyReplacement());
                supplyReplacementRepository.save(supplyReplacement);
            });
        }
        if(planCheckDTO.getErrorReportDTOS() != null && planCheckDTO.getErrorReportDTOS().size() > 0){
            planCheckDTO.getErrorReportDTOS().forEach(item -> {
                item.setCreatedBy(userName);
                item.setCreatedAt(java.time.LocalDateTime.now());
                item.setUpdatedAt(java.time.LocalDateTime.now());
                item.setPlanResult(planResult);
                ErrorReport errorReport = errorReportService.mapToEntity(item, new  ErrorReport());
                errorReportRepository.save(errorReport);
            });
        }
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
        planResultDTO.setDateTest(planResult.getDateTest());
        planResultDTO.setUserTest(planResult.getUserTest());
        planResultDTO.setCreatedAt(planResult.getCreatedAt());
        planResultDTO.setUpdatedAt(planResult.getUpdatedAt());
        planResultDTO.setCreatedBy(planResult.getCreatedBy());
        planResultDTO.setUpdatedBy(planResult.getUpdatedBy());
        planResultDTO.setStatus(planResult.getStatus());
        planResultDTO.setStatusRepair(planResult.getStatusRepair());
        // sao chep plandetail co kiem soat
        if(planResult.getPlanDetail() != null){
            PlanDetail planDetailCopy = new PlanDetail();
            planDetailCopy.setId(planResult.getPlanDetail().getId());
            planDetailCopy.setSerial(planResult.getPlanDetail().getSerial());
            planDetailCopy.setCreatedAt(planResult.getPlanDetail().getCreatedAt());
            planDetailCopy.setUpdatedAt(planResult.getPlanDetail().getUpdatedAt());
            planDetailCopy.setCreatedBy(planResult.getPlanDetail().getCreatedBy());
            planDetailCopy.setUpdatedBy(planResult.getPlanDetail().getUpdatedBy());
            planDetailCopy.setManager(planResult.getPlanDetail().getManager());
            planDetailCopy.setStatus(planResult.getPlanDetail().getStatus());
            // Xóa các quan hệ con để tránh vòng lặp
            planDetailCopy.setPlan(null);
            planDetailCopy.setDevice(null);
            planDetailCopy.setDeviceGroup(null);
            planDetailCopy.setSampleReport(null);
            planResultDTO.setPlanDetail(planDetailCopy);
        }else {
            planResultDTO.setPlanDetail(null);
        }
        return planResultDTO;
    }

    private PlanResult mapToEntity(final PlanResultDTO planResultDTO, final PlanResult planResult) {
        planResult.setCode(planResultDTO.getCode());
        planResult.setNote(planResultDTO.getNote());
        planResult.setDateTest(planResultDTO.getDateTest());
        planResult.setUserTest(planResultDTO.getUserTest());
        planResult.setCreatedAt(planResultDTO.getCreatedAt());
        planResult.setUpdatedAt(planResultDTO.getUpdatedAt());
        planResult.setCreatedBy(planResultDTO.getCreatedBy());
        planResult.setUpdatedBy(planResultDTO.getUpdatedBy());
        planResult.setStatus(planResultDTO.getStatus());
        planResult.setStatusRepair(planResultDTO.getStatusRepair());
        final PlanDetail planDetail = planResultDTO.getPlanDetail() == null ? null : planDetailRepository.findById(planResultDTO.getPlanDetail().getId())
                .orElseThrow(() -> new NotFoundException("planDetail not found"));
        planResult.setPlanDetail(planDetail);
        return planResult;
    }

}
