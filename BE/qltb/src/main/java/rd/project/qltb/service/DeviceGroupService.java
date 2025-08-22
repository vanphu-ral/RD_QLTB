package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.DeviceGroup;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.SampleReport;
import rd.project.qltb.model.DeviceGroupDTO;
import rd.project.qltb.repos.DeviceGroupRepository;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.repos.PlanRepository;
import rd.project.qltb.repos.SampleReportRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class DeviceGroupService {

    private final DeviceGroupRepository deviceGroupRepository;
    private final DeviceRepository deviceRepository;
    private final SampleReportRepository sampleReportRepository;
    private final PlanRepository planRepository;

    public DeviceGroupService(final DeviceGroupRepository deviceGroupRepository,
            final DeviceRepository deviceRepository,
            final SampleReportRepository sampleReportRepository,
            final PlanRepository planRepository) {
        this.deviceGroupRepository = deviceGroupRepository;
        this.deviceRepository = deviceRepository;
        this.sampleReportRepository = sampleReportRepository;
        this.planRepository = planRepository;
    }

    public List<DeviceGroupDTO> findAll() {
        final List<DeviceGroup> deviceGroups = deviceGroupRepository.findAll(Sort.by("id"));
        return deviceGroups.stream()
                .map(deviceGroup -> mapToDTO(deviceGroup, new DeviceGroupDTO()))
                .toList();
    }

    public DeviceGroupDTO get(final Integer id) {
        return deviceGroupRepository.findById(id)
                .map(deviceGroup -> mapToDTO(deviceGroup, new DeviceGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final DeviceGroupDTO deviceGroupDTO) {
        final DeviceGroup deviceGroup = new DeviceGroup();
        mapToEntity(deviceGroupDTO, deviceGroup);
        return deviceGroupRepository.save(deviceGroup).getId();
    }

    public void update(final Integer id, final DeviceGroupDTO deviceGroupDTO) {
        final DeviceGroup deviceGroup = deviceGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceGroupDTO, deviceGroup);
        deviceGroupRepository.save(deviceGroup);
    }

    public void delete(final Integer id) {
        deviceGroupRepository.deleteById(id);
    }

    private DeviceGroupDTO mapToDTO(final DeviceGroup deviceGroup,
            final DeviceGroupDTO deviceGroupDTO) {
        deviceGroupDTO.setId(deviceGroup.getId());
        deviceGroupDTO.setCode(deviceGroup.getCode());
        deviceGroupDTO.setName(deviceGroup.getName());
        deviceGroupDTO.setDescription(deviceGroup.getDescription());
        deviceGroupDTO.setCreatedAt(deviceGroup.getCreatedAt());
        deviceGroupDTO.setUpdatedAt(deviceGroup.getUpdatedAt());
        deviceGroupDTO.setCreatedBy(deviceGroup.getCreatedBy());
        return deviceGroupDTO;
    }

    private DeviceGroup mapToEntity(final DeviceGroupDTO deviceGroupDTO,
            final DeviceGroup deviceGroup) {
        deviceGroup.setCode(deviceGroupDTO.getCode());
        deviceGroup.setName(deviceGroupDTO.getName());
        deviceGroup.setDescription(deviceGroupDTO.getDescription());
        deviceGroup.setCreatedAt(deviceGroupDTO.getCreatedAt());
        deviceGroup.setUpdatedAt(deviceGroupDTO.getUpdatedAt());
        deviceGroup.setCreatedBy(deviceGroupDTO.getCreatedBy());
        return deviceGroup;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final DeviceGroup deviceGroup = deviceGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Device groupDevice = deviceRepository.findFirstByGroup(deviceGroup);
        if (groupDevice != null) {
            referencedWarning.setKey("deviceGroup.device.group.referenced");
            referencedWarning.addParam(groupDevice.getId());
            return referencedWarning;
        }
        final SampleReport deviceGroupSampleReport = sampleReportRepository.findFirstByDeviceGroup(deviceGroup);
        if (deviceGroupSampleReport != null) {
            referencedWarning.setKey("deviceGroup.sampleReport.deviceGroup.referenced");
            referencedWarning.addParam(deviceGroupSampleReport.getId());
            return referencedWarning;
        }
        final Plan deviceGroupPlan = planRepository.findFirstByDeviceGroup(deviceGroup);
        if (deviceGroupPlan != null) {
            referencedWarning.setKey("deviceGroup.plan.deviceGroup.referenced");
            referencedWarning.addParam(deviceGroupPlan.getId());
            return referencedWarning;
        }
        return null;
    }

}
