package io.rd.qltb.service;

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

    public DeviceGroupService(final DeviceGroupRepository deviceGroupRepository,
            final ApplicationEventPublisher publisher) {
        this.deviceGroupRepository = deviceGroupRepository;
        this.publisher = publisher;
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
        final DeviceGroup deviceGroup = deviceGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteDeviceGroup(id));
        deviceGroupRepository.delete(deviceGroup);
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
        deviceGroupDTO.setUpdatedBy(deviceGroup.getUpdatedBy());
        deviceGroupDTO.setStatus(deviceGroup.getStatus());
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
