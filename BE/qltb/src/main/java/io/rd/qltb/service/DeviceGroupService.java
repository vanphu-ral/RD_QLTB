package io.rd.qltb.service;

import io.rd.qltb.config.GlobalConfig;
import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.model.DeviceGroupDTO;
import io.rd.qltb.repos.DeviceGroupRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DeviceGroupService {

    private final DeviceGroupRepository deviceGroupRepository;
    private final ApplicationEventPublisher publisher;
    private final GlobalConfig globalConfig;

    public DeviceGroupService(final DeviceGroupRepository deviceGroupRepository,
                              final ApplicationEventPublisher publisher, GlobalConfig globalConfig) {
        this.deviceGroupRepository = deviceGroupRepository;
        this.publisher = publisher;
        this.globalConfig = globalConfig;
    }

    public List<DeviceGroupDTO> findAll() {
        final List<DeviceGroup> deviceGroups = deviceGroupRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return deviceGroups.stream()
                .map(deviceGroup -> mapToDTO(deviceGroup, new DeviceGroupDTO()))
                .toList();
    }

    public DeviceGroupDTO get(final Long id) {
        return deviceGroupRepository.findById(id)
                .map(deviceGroup -> mapToDTO(deviceGroup, new DeviceGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceGroupDTO deviceGroupDTO) {
        final DeviceGroup deviceGroup = new DeviceGroup();
        mapToEntity(deviceGroupDTO, deviceGroup);
        DeviceGroup savedDeviceGroup = deviceGroupRepository.save(deviceGroup);
        savedDeviceGroup.setCode(deviceGroupDTO.getCode()+"-"+globalConfig.createNumberPrefix(savedDeviceGroup.getId(),4));
        return deviceGroupRepository.save(savedDeviceGroup).getId();
    }

    public void update(final Long id, final DeviceGroupDTO deviceGroupDTO) {
        final DeviceGroup deviceGroup = deviceGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceGroupDTO, deviceGroup);
        deviceGroupRepository.save(deviceGroup);
    }

    public void delete(final Long id) {
        final DeviceGroup deviceGroup = deviceGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteDeviceGroup(id));
        deviceGroupRepository.delete(deviceGroup);
    }

    public DeviceGroupDTO mapToDTO(final DeviceGroup deviceGroup,
            final DeviceGroupDTO deviceGroupDTO) {
        deviceGroupDTO.setId(deviceGroup.getId());
        deviceGroupDTO.setCode(deviceGroup.getCode());
        deviceGroupDTO.setName(deviceGroup.getName());
        deviceGroupDTO.setDescription(deviceGroup.getDescription());
        deviceGroupDTO.setCreatedAt(deviceGroup.getCreatedAt());
        deviceGroupDTO.setUpdatedAt(deviceGroup.getUpdatedAt());
        deviceGroupDTO.setCreatedBy(deviceGroup.getCreatedBy());
        deviceGroupDTO.setUpdatedBy(deviceGroup.getUpdatedBy());
        deviceGroupDTO.setStatus(deviceGroup.getStatus());
        if(deviceGroup.getGroupDevices() != null) {
            for(Device device : deviceGroup.getGroupDevices()) {
                device.setGroup(null);
                // Loại bỏ các tham chiếu line
                device.getLine().setTeam(null);
                device.getLine().setLineDevices(null);

                // Loại bỏ các tham chiếu branch
                device.getBranch().setFactory(null);
                device.getBranch().setBranchTeams(null);
                device.getBranch().setBranchDevices(null);
                device.getBranch().setSampleReports(null);

                device.setDeviceDeviceParameterUses(null);
                device.setDeviceDeviceRelocationHistories(null);
                device.setDeviceDeviceSupplyUsages(null);
                device.setDevicePlanDetails(null);
                // Loại bỏ các tham chiếu team
                device.getTeam().setBranch(null);
                device.getTeam().setTeamLines(null);
                device.getTeam().setTeamDevices(null);
            }
            deviceGroupDTO.setGroupDevices(
                    deviceGroup.getGroupDevices().stream().toList());
        }
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
        deviceGroup.setUpdatedBy(deviceGroupDTO.getUpdatedBy());
        deviceGroup.setStatus(deviceGroupDTO.getStatus());
        return deviceGroup;
    }

}
