package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.DeviceGroup;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.PlanResultDetail;
import rd.project.qltb.domain.SampleReport;
import rd.project.qltb.model.SampleReportDTO;
import rd.project.qltb.repos.DeviceGroupRepository;
import rd.project.qltb.repos.PlanRepository;
import rd.project.qltb.repos.PlanResultDetailRepository;
import rd.project.qltb.repos.SampleReportRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class SampleReportService {

    private final SampleReportRepository sampleReportRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final PlanRepository planRepository;
    private final PlanResultDetailRepository planResultDetailRepository;

    public SampleReportService(final SampleReportRepository sampleReportRepository,
            final DeviceGroupRepository deviceGroupRepository, final PlanRepository planRepository,
            final PlanResultDetailRepository planResultDetailRepository) {
        this.sampleReportRepository = sampleReportRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.planRepository = planRepository;
        this.planResultDetailRepository = planResultDetailRepository;
    }

    public List<SampleReportDTO> findAll() {
        final List<SampleReport> sampleReports = sampleReportRepository.findAll(Sort.by("id"));
        return sampleReports.stream()
                .map(sampleReport -> mapToDTO(sampleReport, new SampleReportDTO()))
                .toList();
    }

    public SampleReportDTO get(final Long id) {
        return sampleReportRepository.findById(id)
                .map(sampleReport -> mapToDTO(sampleReport, new SampleReportDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final SampleReportDTO sampleReportDTO) {
        final SampleReport sampleReport = new SampleReport();
        mapToEntity(sampleReportDTO, sampleReport);
        return sampleReportRepository.save(sampleReport).getId();
    }

    public void update(final Long id, final SampleReportDTO sampleReportDTO) {
        final SampleReport sampleReport = sampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(sampleReportDTO, sampleReport);
        sampleReportRepository.save(sampleReport);
    }

    public void delete(final Long id) {
        sampleReportRepository.deleteById(id);
    }

    private SampleReportDTO mapToDTO(final SampleReport sampleReport,
            final SampleReportDTO sampleReportDTO) {
        sampleReportDTO.setId(sampleReport.getId());
        sampleReportDTO.setCode(sampleReport.getCode());
        sampleReportDTO.setName(sampleReport.getName());
        sampleReportDTO.setFrequency(sampleReport.getFrequency());
        sampleReportDTO.setType(sampleReport.getType());
        sampleReportDTO.setStatus(sampleReport.getStatus());
        sampleReportDTO.setCreatedAt(sampleReport.getCreatedAt());
        sampleReportDTO.setUpdatedAt(sampleReport.getUpdatedAt());
        sampleReportDTO.setCreatedBy(sampleReport.getCreatedBy());
        sampleReportDTO.setDeviceGroup(sampleReport.getDeviceGroup() == null ? null : sampleReport.getDeviceGroup().getId());
        return sampleReportDTO;
    }

    private SampleReport mapToEntity(final SampleReportDTO sampleReportDTO,
            final SampleReport sampleReport) {
        sampleReport.setCode(sampleReportDTO.getCode());
        sampleReport.setName(sampleReportDTO.getName());
        sampleReport.setFrequency(sampleReportDTO.getFrequency());
        sampleReport.setType(sampleReportDTO.getType());
        sampleReport.setStatus(sampleReportDTO.getStatus());
        sampleReport.setCreatedAt(sampleReportDTO.getCreatedAt());
        sampleReport.setUpdatedAt(sampleReportDTO.getUpdatedAt());
        sampleReport.setCreatedBy(sampleReportDTO.getCreatedBy());
        final DeviceGroup deviceGroup = sampleReportDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(sampleReportDTO.getDeviceGroup())
                .orElseThrow(() -> new NotFoundException("deviceGroup not found"));
        sampleReport.setDeviceGroup(deviceGroup);
        return sampleReport;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final SampleReport sampleReport = sampleReportRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Plan sampleReportPlan = planRepository.findFirstBySampleReport(sampleReport);
        if (sampleReportPlan != null) {
            referencedWarning.setKey("sampleReport.plan.sampleReport.referenced");
            referencedWarning.addParam(sampleReportPlan.getId());
            return referencedWarning;
        }
        final PlanResultDetail sampleReportPlanResultDetail = planResultDetailRepository.findFirstBySampleReport(sampleReport);
        if (sampleReportPlanResultDetail != null) {
            referencedWarning.setKey("sampleReport.planResultDetail.sampleReport.referenced");
            referencedWarning.addParam(sampleReportPlanResultDetail.getId());
            return referencedWarning;
        }
        return null;
    }

}
