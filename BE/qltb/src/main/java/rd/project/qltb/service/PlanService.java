package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.DeviceGroup;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.PlanDetail;
import rd.project.qltb.domain.PlanSupplie;
import rd.project.qltb.domain.PlanType;
import rd.project.qltb.domain.SampleReport;
import rd.project.qltb.model.PlanDTO;
import rd.project.qltb.repos.DeviceGroupRepository;
import rd.project.qltb.repos.PlanDetailRepository;
import rd.project.qltb.repos.PlanRepository;
import rd.project.qltb.repos.PlanSupplieRepository;
import rd.project.qltb.repos.PlanTypeRepository;
import rd.project.qltb.repos.SampleReportRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanTypeRepository planTypeRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final SampleReportRepository sampleReportRepository;
    private final PlanDetailRepository planDetailRepository;
    private final PlanSupplieRepository planSupplieRepository;

    public PlanService(final PlanRepository planRepository,
            final PlanTypeRepository planTypeRepository,
            final DeviceGroupRepository deviceGroupRepository,
            final SampleReportRepository sampleReportRepository,
            final PlanDetailRepository planDetailRepository,
            final PlanSupplieRepository planSupplieRepository) {
        this.planRepository = planRepository;
        this.planTypeRepository = planTypeRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.sampleReportRepository = sampleReportRepository;
        this.planDetailRepository = planDetailRepository;
        this.planSupplieRepository = planSupplieRepository;
    }

    public List<PlanDTO> findAll() {
        final List<Plan> plans = planRepository.findAll(Sort.by("id"));
        return plans.stream()
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .toList();
    }

    public PlanDTO get(final Long id) {
        return planRepository.findById(id)
                .map(plan -> mapToDTO(plan, new PlanDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanDTO planDTO) {
        final Plan plan = new Plan();
        mapToEntity(planDTO, plan);
        return planRepository.save(plan).getId();
    }

    public void update(final Long id, final PlanDTO planDTO) {
        final Plan plan = planRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planDTO, plan);
        planRepository.save(plan);
    }

    public void delete(final Long id) {
        planRepository.deleteById(id);
    }

    private PlanDTO mapToDTO(final Plan plan, final PlanDTO planDTO) {
        planDTO.setId(plan.getId());
        planDTO.setName(plan.getName());
        planDTO.setFactoryId(plan.getFactoryId());
        planDTO.setBranchId(plan.getBranchId());
        planDTO.setFrequency(plan.getFrequency());
        planDTO.setPlanNumber(plan.getPlanNumber());
        planDTO.setNote(plan.getNote());
        planDTO.setCreatedBy(plan.getCreatedBy());
        planDTO.setCreatedAt(plan.getCreatedAt());
        planDTO.setUpdatedAt(plan.getUpdatedAt());
        planDTO.setCreatedBy1(plan.getCreatedBy1());
        planDTO.setStatus(plan.getStatus());
        planDTO.setPlanType(plan.getPlanType() == null ? null : plan.getPlanType().getId());
        planDTO.setDeviceGroup(plan.getDeviceGroup() == null ? null : plan.getDeviceGroup().getId());
        planDTO.setSampleReport(plan.getSampleReport() == null ? null : plan.getSampleReport().getId());
        return planDTO;
    }

    private Plan mapToEntity(final PlanDTO planDTO, final Plan plan) {
        plan.setName(planDTO.getName());
        plan.setFactoryId(planDTO.getFactoryId());
        plan.setBranchId(planDTO.getBranchId());
        plan.setFrequency(planDTO.getFrequency());
        plan.setPlanNumber(planDTO.getPlanNumber());
        plan.setNote(planDTO.getNote());
        plan.setCreatedBy(planDTO.getCreatedBy());
        plan.setCreatedAt(planDTO.getCreatedAt());
        plan.setUpdatedAt(planDTO.getUpdatedAt());
        plan.setCreatedBy1(planDTO.getCreatedBy1());
        plan.setStatus(planDTO.getStatus());
        final PlanType planType = planDTO.getPlanType() == null ? null : planTypeRepository.findById(planDTO.getPlanType())
                .orElseThrow(() -> new NotFoundException("planType not found"));
        plan.setPlanType(planType);
        final DeviceGroup deviceGroup = planDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(planDTO.getDeviceGroup())
                .orElseThrow(() -> new NotFoundException("deviceGroup not found"));
        plan.setDeviceGroup(deviceGroup);
        final SampleReport sampleReport = planDTO.getSampleReport() == null ? null : sampleReportRepository.findById(planDTO.getSampleReport())
                .orElseThrow(() -> new NotFoundException("sampleReport not found"));
        plan.setSampleReport(sampleReport);
        return plan;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Plan plan = planRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanDetail planPlanDetail = planDetailRepository.findFirstByPlan(plan);
        if (planPlanDetail != null) {
            referencedWarning.setKey("plan.planDetail.plan.referenced");
            referencedWarning.addParam(planPlanDetail.getId());
            return referencedWarning;
        }
        final PlanSupplie planPlanSupplie = planSupplieRepository.findFirstByPlan(plan);
        if (planPlanSupplie != null) {
            referencedWarning.setKey("plan.planSupplie.plan.referenced");
            referencedWarning.addParam(planPlanSupplie.getId());
            return referencedWarning;
        }
        return null;
    }

}
