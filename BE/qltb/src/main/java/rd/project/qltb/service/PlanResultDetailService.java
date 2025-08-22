package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.PlanResult;
import rd.project.qltb.domain.PlanResultDetail;
import rd.project.qltb.domain.SampleReport;
import rd.project.qltb.model.PlanResultDetailDTO;
import rd.project.qltb.repos.PlanResultDetailRepository;
import rd.project.qltb.repos.PlanResultRepository;
import rd.project.qltb.repos.SampleReportRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class PlanResultDetailService {

    private final PlanResultDetailRepository planResultDetailRepository;
    private final SampleReportRepository sampleReportRepository;
    private final PlanResultRepository planResultRepository;

    public PlanResultDetailService(final PlanResultDetailRepository planResultDetailRepository,
            final SampleReportRepository sampleReportRepository,
            final PlanResultRepository planResultRepository) {
        this.planResultDetailRepository = planResultDetailRepository;
        this.sampleReportRepository = sampleReportRepository;
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
        planResultDetailRepository.deleteById(id);
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
        planResultDetailDTO.setStatus(planResultDetail.getStatus());
        planResultDetailDTO.setCreatedAt(planResultDetail.getCreatedAt());
        planResultDetailDTO.setUpdatedAt(planResultDetail.getUpdatedAt());
        planResultDetailDTO.setCreatedBy(planResultDetail.getCreatedBy());
        planResultDetailDTO.setSampleReport(planResultDetail.getSampleReport() == null ? null : planResultDetail.getSampleReport().getId());
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
        planResultDetail.setStatus(planResultDetailDTO.getStatus());
        planResultDetail.setCreatedAt(planResultDetailDTO.getCreatedAt());
        planResultDetail.setUpdatedAt(planResultDetailDTO.getUpdatedAt());
        planResultDetail.setCreatedBy(planResultDetailDTO.getCreatedBy());
        final SampleReport sampleReport = planResultDetailDTO.getSampleReport() == null ? null : sampleReportRepository.findById(planResultDetailDTO.getSampleReport())
                .orElseThrow(() -> new NotFoundException("sampleReport not found"));
        planResultDetail.setSampleReport(sampleReport);
        return planResultDetail;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final PlanResultDetail planResultDetail = planResultDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanResult planResultDetailPlanResult = planResultRepository.findFirstByPlanResultDetail(planResultDetail);
        if (planResultDetailPlanResult != null) {
            referencedWarning.setKey("planResultDetail.planResult.planResultDetail.referenced");
            referencedWarning.addParam(planResultDetailPlanResult.getId());
            return referencedWarning;
        }
        return null;
    }

}
