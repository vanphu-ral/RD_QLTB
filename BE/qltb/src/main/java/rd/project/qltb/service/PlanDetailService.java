package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.PlanDetail;
import rd.project.qltb.domain.PlanResult;
import rd.project.qltb.model.PlanDetailDTO;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.repos.PlanDetailRepository;
import rd.project.qltb.repos.PlanRepository;
import rd.project.qltb.repos.PlanResultRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class PlanDetailService {

    private final PlanDetailRepository planDetailRepository;
    private final PlanRepository planRepository;
    private final DeviceRepository deviceRepository;
    private final PlanResultRepository planResultRepository;

    public PlanDetailService(final PlanDetailRepository planDetailRepository,
            final PlanRepository planRepository, final DeviceRepository deviceRepository,
            final PlanResultRepository planResultRepository) {
        this.planDetailRepository = planDetailRepository;
        this.planRepository = planRepository;
        this.deviceRepository = deviceRepository;
        this.planResultRepository = planResultRepository;
    }

    public List<PlanDetailDTO> findAll() {
        final List<PlanDetail> planDetails = planDetailRepository.findAll(Sort.by("id"));
        return planDetails.stream()
                .map(planDetail -> mapToDTO(planDetail, new PlanDetailDTO()))
                .toList();
    }

    public PlanDetailDTO get(final Long id) {
        return planDetailRepository.findById(id)
                .map(planDetail -> mapToDTO(planDetail, new PlanDetailDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PlanDetailDTO planDetailDTO) {
        final PlanDetail planDetail = new PlanDetail();
        mapToEntity(planDetailDTO, planDetail);
        return planDetailRepository.save(planDetail).getId();
    }

    public void update(final Long id, final PlanDetailDTO planDetailDTO) {
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(planDetailDTO, planDetail);
        planDetailRepository.save(planDetail);
    }

    public void delete(final Long id) {
        planDetailRepository.deleteById(id);
    }

    private PlanDetailDTO mapToDTO(final PlanDetail planDetail, final PlanDetailDTO planDetailDTO) {
        planDetailDTO.setId(planDetail.getId());
        planDetailDTO.setFrequency(planDetail.getFrequency());
        planDetailDTO.setUserSign(planDetail.getUserSign());
        planDetailDTO.setCreatedAt(planDetail.getCreatedAt());
        planDetailDTO.setUpdatedAt(planDetail.getUpdatedAt());
        planDetailDTO.setCreatedBy(planDetail.getCreatedBy());
        planDetailDTO.setUser(planDetail.getUser());
        planDetailDTO.setStatus(planDetail.getStatus());
        planDetailDTO.setPlan(planDetail.getPlan() == null ? null : planDetail.getPlan().getId());
        planDetailDTO.setDevice(planDetail.getDevice() == null ? null : planDetail.getDevice().getId());
        return planDetailDTO;
    }

    private PlanDetail mapToEntity(final PlanDetailDTO planDetailDTO, final PlanDetail planDetail) {
        planDetail.setFrequency(planDetailDTO.getFrequency());
        planDetail.setUserSign(planDetailDTO.getUserSign());
        planDetail.setCreatedAt(planDetailDTO.getCreatedAt());
        planDetail.setUpdatedAt(planDetailDTO.getUpdatedAt());
        planDetail.setCreatedBy(planDetailDTO.getCreatedBy());
        planDetail.setUser(planDetailDTO.getUser());
        planDetail.setStatus(planDetailDTO.getStatus());
        final Plan plan = planDetailDTO.getPlan() == null ? null : planRepository.findById(planDetailDTO.getPlan())
                .orElseThrow(() -> new NotFoundException("plan not found"));
        planDetail.setPlan(plan);
        final Device device = planDetailDTO.getDevice() == null ? null : deviceRepository.findById(planDetailDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        planDetail.setDevice(device);
        return planDetail;
    }

    public ReferencedWarning getReferencedWarning(final Long id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final PlanResult planDetailPlanResult = planResultRepository.findFirstByPlanDetail(planDetail);
        if (planDetailPlanResult != null) {
            referencedWarning.setKey("planDetail.planResult.planDetail.referenced");
            referencedWarning.addParam(planDetailPlanResult.getId());
            return referencedWarning;
        }
        return null;
    }

}
