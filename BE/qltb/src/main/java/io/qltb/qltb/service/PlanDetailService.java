package io.qltb.qltb.service;

import io.qltb.qltb.domain.Device;
import io.qltb.qltb.domain.DeviceGroup;
import io.qltb.qltb.domain.Plan;
import io.qltb.qltb.domain.PlanDetail;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.events.BeforeDeleteDeviceGroup;
import io.qltb.qltb.events.BeforeDeletePlan;
import io.qltb.qltb.events.BeforeDeletePlanDetail;
import io.qltb.qltb.model.PlanDetailDTO;
import io.qltb.qltb.repos.DeviceGroupRepository;
import io.qltb.qltb.repos.DeviceRepository;
import io.qltb.qltb.repos.PlanDetailRepository;
import io.qltb.qltb.repos.PlanRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanDetailService {

    private final PlanDetailRepository planDetailRepository;
    private final PlanRepository planRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final ApplicationEventPublisher publisher;

    public PlanDetailService(final PlanDetailRepository planDetailRepository,
            final PlanRepository planRepository, final DeviceRepository deviceRepository,
            final DeviceGroupRepository deviceGroupRepository,
            final ApplicationEventPublisher publisher) {
        this.planDetailRepository = planDetailRepository;
        this.planRepository = planRepository;
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.publisher = publisher;
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
        final PlanDetail planDetail = planDetailRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeletePlanDetail(id));
        planDetailRepository.delete(planDetail);
    }

    private PlanDetailDTO mapToDTO(final PlanDetail planDetail, final PlanDetailDTO planDetailDTO) {
        planDetailDTO.setId(planDetail.getId());
        planDetailDTO.setSampleReporId(planDetail.getSampleReporId());
        planDetailDTO.setCreatedAt(planDetail.getCreatedAt());
        planDetailDTO.setUpdatedAt(planDetail.getUpdatedAt());
        planDetailDTO.setCreatedBy(planDetail.getCreatedBy());
        planDetailDTO.setUpdatedBy(planDetail.getUpdatedBy());
        planDetailDTO.setUser(planDetail.getUser());
        planDetailDTO.setStatus(planDetail.getStatus());
        planDetailDTO.setPlan(planDetail.getPlan() == null ? null : planDetail.getPlan().getId());
        planDetailDTO.setDevice(planDetail.getDevice() == null ? null : planDetail.getDevice().getId());
        planDetailDTO.setDeviceGroup(planDetail.getDeviceGroup() == null ? null : planDetail.getDeviceGroup().getId());
        return planDetailDTO;
    }

    private PlanDetail mapToEntity(final PlanDetailDTO planDetailDTO, final PlanDetail planDetail) {
        planDetail.setSampleReporId(planDetailDTO.getSampleReporId());
        planDetail.setCreatedAt(planDetailDTO.getCreatedAt());
        planDetail.setUpdatedAt(planDetailDTO.getUpdatedAt());
        planDetail.setCreatedBy(planDetailDTO.getCreatedBy());
        planDetail.setUpdatedBy(planDetailDTO.getUpdatedBy());
        planDetail.setUser(planDetailDTO.getUser());
        planDetail.setStatus(planDetailDTO.getStatus());
        final Plan plan = planDetailDTO.getPlan() == null ? null : planRepository.findById(planDetailDTO.getPlan())
                .orElseThrow(() -> new NotFoundException("plan not found"));
        planDetail.setPlan(plan);
        final Device device = planDetailDTO.getDevice() == null ? null : deviceRepository.findById(planDetailDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        planDetail.setDevice(device);
        final DeviceGroup deviceGroup = planDetailDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(planDetailDTO.getDeviceGroup())
                .orElseThrow(() -> new NotFoundException("deviceGroup not found"));
        planDetail.setDeviceGroup(deviceGroup);
        return planDetail;
    }

    @EventListener(BeforeDeletePlan.class)
    public void on(final BeforeDeletePlan event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanDetail planPlanDetail = planDetailRepository.findFirstByPlanId(event.getId());
        if (planPlanDetail != null) {
            referencedException.setKey("plan.planDetail.plan.referenced");
            referencedException.addParam(planPlanDetail.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanDetail devicePlanDetail = planDetailRepository.findFirstByDeviceId(event.getId());
        if (devicePlanDetail != null) {
            referencedException.setKey("device.planDetail.device.referenced");
            referencedException.addParam(devicePlanDetail.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteDeviceGroup.class)
    public void on(final BeforeDeleteDeviceGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final PlanDetail deviceGroupPlanDetail = planDetailRepository.findFirstByDeviceGroupId(event.getId());
        if (deviceGroupPlanDetail != null) {
            referencedException.setKey("deviceGroup.planDetail.deviceGroup.referenced");
            referencedException.addParam(deviceGroupPlanDetail.getId());
            throw referencedException;
        }
    }

}
