package io.qltb.qltb.service;

import io.qltb.qltb.domain.DeviceGroup;
import io.qltb.qltb.events.BeforeDeleteDeviceGroup;
import io.qltb.qltb.model.DeviceGroupDTO;
import io.qltb.qltb.repos.DeviceGroupRepository;
import io.qltb.qltb.util.NotFoundException;
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

    public DeviceGroupDTO get(final Long id) {
        return deviceGroupRepository.findById(id)
                .map(deviceGroup -> mapToDTO(deviceGroup, new DeviceGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceGroupDTO deviceGroupDTO) {
        final DeviceGroup deviceGroup = new DeviceGroup();
        mapToEntity(deviceGroupDTO, deviceGroup);
        return deviceGroupRepository.save(deviceGroup).getId();
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

    public boolean codeExists(final String code) {
        return deviceGroupRepository.existsByCodeIgnoreCase(code);
    }

}
