package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeletePlanResult;
import io.rd.qltb.model.PlanCheckDTO;
import io.rd.qltb.model.PlanResultDTO;
import io.rd.qltb.model.SupplyReplacementDTO;
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
    public PlanCheckDTO getDetail(Long planResultId){
        PlanCheckDTO planCheckDTO = new PlanCheckDTO();
        planCheckDTO.setPlanResult(get(planResultId));
        List<PlanResultDetail> planResultDetails = planResultDetailRepository.findByPlanResultId(planResultId);
        if(planResultDetails != null && planResultDetails.size() > 0){
            planCheckDTO.setPlanResultDetail(planResultDetails.stream().map(item -> planResultDetailService.mapToDTO(item, new io.rd.qltb.model.PlanResultDetailDTO())).toList());
        }
        List<ErrorReport> errorReports = errorReportRepository.findByPlanResultId(planResultId);
        if(errorReports != null && errorReports.size() > 0){
            planCheckDTO.setErrorReport(errorReports.stream().map(item -> errorReportService.mapToDTO(item, new io.rd.qltb.model.ErrorReportDTO())).toList());
        }
        List<SupplyReplacement> supplyReplacements = supplyReplacementRepository.findByPlanResultId(planResultId);
        if(supplyReplacements != null && supplyReplacements.size() > 0){
            planCheckDTO.setSupplyReplacement(supplyReplacements.stream().map(item -> supplyReplacementService.mapToDTO(item, new SupplyReplacementDTO())).toList());
        }
        return planCheckDTO;
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
        PlanResult planResult = planResultRepository.findById(planCheckDTO.getPlanResult().getId()).orElse(new PlanResult());
        if (planCheckDTO.getPlanResultDetail() != null && planCheckDTO.getPlanResultDetail().size() > 0) {
            planCheckDTO.getPlanResultDetail().forEach(item -> {
                if(item.getId() != null){
                    PlanResultDetail existingDetail = planResultDetailService.mapToEntity(item, planResultDetailRepository.findById(item.getId()).orElse(new PlanResultDetail()));
                    item.setPlanResult(existingDetail.getPlanResult());
                    item.setUpdatedAt(java.time.LocalDateTime.now());
                    item.setUpdatedBy(userName);
                    PlanResultDetail planResultDetail = planResultDetailService.mapToEntity(item, existingDetail);
                    planResultDetailRepository.save(planResultDetail);
                }else{
                item.setPlanResult(planResult);
                item.setCreatedBy(userName);
                item.setCreatedAt(java.time.LocalDateTime.now());
                item.setUpdatedAt(java.time.LocalDateTime.now());
                PlanResultDetail planResultDetail = planResultDetailService.mapToEntity(item, new PlanResultDetail());
                planResultDetailRepository.save(planResultDetail);
                }
            });
        }
        if(planCheckDTO.getSupplyReplacement() != null && planCheckDTO.getSupplyReplacement().size() > 0){
            planCheckDTO.getSupplyReplacement().forEach(item -> {
                if(item.getId() != null){
                    SupplyReplacement supplyReplacement = supplyReplacementService.mapToEntity(item, supplyReplacementRepository.findById(item.getId()).orElse(new SupplyReplacement()));
                    item.setPlanResult(supplyReplacement.getPlanResult());
                    item.setUpdatedAt(java.time.LocalDateTime.now());
                    item.setUpdatedBy(userName);
                    SupplyReplacement supplyReplacementSave = supplyReplacementService.mapToEntity(item, new SupplyReplacement());
                    supplyReplacementRepository.save(supplyReplacementSave);
                }else {
                    item.setCreatedBy(userName);
                    item.setCreatedAt(java.time.LocalDateTime.now());
                    item.setUpdatedAt(java.time.LocalDateTime.now());
                    item.setPlanResult(planResult);
                    SupplyReplacement supplyReplacement = supplyReplacementService.mapToEntity(item, new SupplyReplacement());
                    supplyReplacementRepository.save(supplyReplacement);
                }
            });
        }
        if(planCheckDTO.getErrorReport() != null && planCheckDTO.getErrorReport().size() > 0){
            planCheckDTO.getErrorReport().forEach(item -> {
                if(item.getId() != null){
                    ErrorReport errorReport = errorReportService.mapToEntity(item, errorReportRepository.findById(item.getId()).orElse(new ErrorReport()));
                    item.setPlanResult(errorReport.getPlanResult());
                    item.setUpdatedAt(java.time.LocalDateTime.now());
                    item.setUpdatedBy(userName);
                    ErrorReport errorReportSave = errorReportService.mapToEntity(item, new ErrorReport());
                    errorReportRepository.save(errorReportSave);
                }else {
                    item.setCreatedBy(userName);
                    item.setCreatedAt(java.time.LocalDateTime.now());
                    item.setUpdatedAt(java.time.LocalDateTime.now());
                    item.setPlanResult(planResult);
                    ErrorReport errorReport = errorReportService.mapToEntity(item, new ErrorReport());
                    errorReportRepository.save(errorReport);
                }
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
    public void deleteAll(Long planResultId){
        List<PlanResultDetail> planResultDetails = planResultDetailRepository.findByPlanResultId(planResultId);
        if(planResultDetails != null && planResultDetails.size() > 0){
            planResultDetailRepository.deleteAll(planResultDetails);
        }
        List<ErrorReport> errorReports = errorReportRepository.findByPlanResultId(planResultId);
        if(errorReports != null && errorReports.size() > 0){
            errorReportRepository.deleteAll(errorReports);
        }
        List<SupplyReplacement> supplyReplacements = supplyReplacementRepository.findByPlanResultId(planResultId);
        if(supplyReplacements != null && supplyReplacements.size() > 0){
            supplyReplacementRepository.deleteAll(supplyReplacements);
        }
        planDetailRepository.deleteById(planResultId);
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
