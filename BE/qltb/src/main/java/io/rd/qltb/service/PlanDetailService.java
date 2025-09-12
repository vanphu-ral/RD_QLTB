package io.rd.qltb.service;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.Plan;
import io.rd.qltb.domain.PlanDetail;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.events.BeforeDeletePlan;
import io.rd.qltb.model.PlanDetailDTO;
import io.rd.qltb.repos.DeviceGroupRepository;
import io.rd.qltb.repos.DeviceRepository;
import io.rd.qltb.repos.PlanDetailRepository;
import io.rd.qltb.repos.PlanRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PlanDetailService {

    private final PlanDetailRepository planDetailRepository;
    private final PlanRepository planRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceGroupRepository deviceGroupRepository;

    public PlanDetailService(final PlanDetailRepository planDetailRepository,
            final PlanRepository planRepository, final DeviceRepository deviceRepository,
            final DeviceGroupRepository deviceGroupRepository) {
        this.planDetailRepository = planDetailRepository;
        this.planRepository = planRepository;
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
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
        planDetailRepository.delete(planDetail);
    }

    private PlanDetailDTO mapToDTO(final PlanDetail planDetail, final PlanDetailDTO dto) {
        dto.setId(planDetail.getId());
        dto.setSampleReporId(planDetail.getSampleReporId());
        dto.setCreatedAt(planDetail.getCreatedAt());
        dto.setUpdatedAt(planDetail.getUpdatedAt());
        dto.setCreatedBy(planDetail.getCreatedBy());
        dto.setUpdatedBy(planDetail.getUpdatedBy());
        dto.setManager(planDetail.getManager());
        dto.setStatus(planDetail.getStatus());

        // Sao chép Plan có kiểm soát
        if (planDetail.getPlan() != null) {
            Plan planCopy = new Plan();
            planCopy.setId(planDetail.getPlan().getId());
            planCopy.setName(planDetail.getPlan().getName());
            planCopy.setFactoryId(planDetail.getPlan().getFactoryId());
            planCopy.setBranchId(planDetail.getPlan().getBranchId());
            planCopy.setFrequency(planDetail.getPlan().getFrequency());
            planCopy.setPlanNumber(planDetail.getPlan().getPlanNumber());
            planCopy.setDescription(planDetail.getPlan().getDescription());
            planCopy.setCreatedBy(planDetail.getPlan().getCreatedBy());
            planCopy.setCreatedAt(planDetail.getPlan().getCreatedAt());
            planCopy.setUpdatedAt(planDetail.getPlan().getUpdatedAt());
            planCopy.setUpdatedBy(planDetail.getPlan().getUpdatedBy());
            planCopy.setStatus(planDetail.getPlan().getStatus());

            // Xóa các quan hệ con
            planCopy.setPlanType(null);
            planCopy.setPlanPlanDetails(null);

            dto.setPlan(planCopy);
        } else {
            dto.setPlan(null);
        }

        // Sao chép Device có kiểm soát
        if (planDetail.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(planDetail.getDevice().getId());
            deviceCopy.setCode(planDetail.getDevice().getCode());
            deviceCopy.setName(planDetail.getDevice().getName());
            deviceCopy.setSerialNumber(planDetail.getDevice().getSerialNumber());
            deviceCopy.setUnit(planDetail.getDevice().getUnit());
            deviceCopy.setStatus(planDetail.getDevice().getStatus());
            deviceCopy.setCreatedAt(planDetail.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(planDetail.getDevice().getUpdatedAt());

            // Xóa các quan hệ con
            deviceCopy.setGroup(null);
            deviceCopy.setLine(null);
            deviceCopy.setBranch(null);
            deviceCopy.setTeam(null);
            deviceCopy.setDeviceDeviceParameterUses(null);
            deviceCopy.setDeviceDeviceRelocationHistories(null);
            deviceCopy.setDeviceDeviceSupplyUsages(null);
            deviceCopy.setDevicePlanDetails(null);

            dto.setDevice(deviceCopy);
        } else {
            dto.setDevice(null);
        }

        // Sao chép DeviceGroup có kiểm soát
        if (planDetail.getDeviceGroup() != null) {
            DeviceGroup groupCopy = new DeviceGroup();
            groupCopy.setId(planDetail.getDeviceGroup().getId());
            groupCopy.setCode(planDetail.getDeviceGroup().getCode());
            groupCopy.setName(planDetail.getDeviceGroup().getName());
            groupCopy.setDescription(planDetail.getDeviceGroup().getDescription());
            groupCopy.setCreatedAt(planDetail.getDeviceGroup().getCreatedAt());
            groupCopy.setUpdatedAt(planDetail.getDeviceGroup().getUpdatedAt());
            groupCopy.setCreatedBy(planDetail.getDeviceGroup().getCreatedBy());
            groupCopy.setUpdatedBy(planDetail.getDeviceGroup().getUpdatedBy());
            groupCopy.setStatus(planDetail.getDeviceGroup().getStatus());

            // Xóa các quan hệ con
            groupCopy.setDeviceGroupSampleReports(null);
            groupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);
            groupCopy.setGroupDevices(null);
            groupCopy.setDeviceGroupPlanDetails(null);

            dto.setDeviceGroup(groupCopy);
        } else {
            dto.setDeviceGroup(null);
        }

        return dto;
    }


    private PlanDetail mapToEntity(final PlanDetailDTO planDetailDTO, final PlanDetail planDetail) {
        planDetail.setSampleReporId(planDetailDTO.getSampleReporId());
        planDetail.setCreatedAt(planDetailDTO.getCreatedAt());
        planDetail.setUpdatedAt(planDetailDTO.getUpdatedAt());
        planDetail.setCreatedBy(planDetailDTO.getCreatedBy());
        planDetail.setUpdatedBy(planDetailDTO.getUpdatedBy());
        planDetail.setManager(planDetailDTO.getManager());
        planDetail.setStatus(planDetailDTO.getStatus());
        final Plan plan = planDetailDTO.getPlan() == null ? null : planRepository.findById(planDetailDTO.getPlan().getId())
                .orElseThrow(() -> new NotFoundException("plan not found"));
        planDetail.setPlan(plan);
        final Device device = planDetailDTO.getDevice() == null ? null : deviceRepository.findById(planDetailDTO.getDevice().getId())
                .orElseThrow(() -> new NotFoundException("device not found"));
        planDetail.setDevice(device);
        final DeviceGroup deviceGroup = planDetailDTO.getDeviceGroup() == null ? null : deviceGroupRepository.findById(planDetailDTO.getDeviceGroup().getId())
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
