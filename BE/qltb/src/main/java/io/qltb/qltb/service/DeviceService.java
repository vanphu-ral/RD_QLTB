package io.qltb.qltb.service;

import io.qltb.qltb.domain.Device;
import io.qltb.qltb.domain.DeviceGroup;
import io.qltb.qltb.domain.Line;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.events.BeforeDeleteDeviceGroup;
import io.qltb.qltb.events.BeforeDeleteLine;
import io.qltb.qltb.model.DeviceDTO;
import io.qltb.qltb.repos.DeviceGroupRepository;
import io.qltb.qltb.repos.DeviceRepository;
import io.qltb.qltb.repos.LineRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final LineRepository lineRepository;
    private final ApplicationEventPublisher publisher;

    public DeviceService(final DeviceRepository deviceRepository,
            final DeviceGroupRepository deviceGroupRepository, final LineRepository lineRepository,
            final ApplicationEventPublisher publisher) {
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.lineRepository = lineRepository;
        this.publisher = publisher;
    }

    public List<DeviceDTO> findAll() {
        final List<Device> devices = deviceRepository.findAll(Sort.by("id"));
        return devices.stream()
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .toList();
    }

    public DeviceDTO get(final Long id) {
        return deviceRepository.findById(id)
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceDTO deviceDTO) {
        final Device device = new Device();
        mapToEntity(deviceDTO, device);
        return deviceRepository.save(device).getId();
    }

    public void update(final Long id, final DeviceDTO deviceDTO) {
        final Device device = deviceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceDTO, device);
        deviceRepository.save(device);
    }

    public void delete(final Long id) {
        final Device device = deviceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteDevice(id));
        deviceRepository.delete(device);
    }

    private DeviceDTO mapToDTO(final Device device, final DeviceDTO deviceDTO) {
        device.getLine().setTeam(null);
        device.getLine().setLineDevices(null);
        device.getLine().setLineForms(null);
        device.getGroup().setGroupDevices(null);
        device.getGroup().setDeviceGroupSampleReports(null);
        device.getGroup().setDeviceGroupPlanDetails(null);
        device.getGroup().setDeviceGroupKeyMappingDeviceSampleReports(null);
        deviceDTO.setId(device.getId());
        deviceDTO.setBracnId(device.getBracnId());
        deviceDTO.setCode(device.getCode());
        deviceDTO.setName(device.getName());
        deviceDTO.setNumMaterialUse(device.getNumMaterialUse());
        deviceDTO.setSerialNumber(device.getSerialNumber());
        deviceDTO.setSource(device.getSource());
        deviceDTO.setInstallationDate(device.getInstallationDate());
        deviceDTO.setMaintenanceCycle(device.getMaintenanceCycle());
        deviceDTO.setDateManufacture(device.getDateManufacture());
        deviceDTO.setUnit(device.getUnit());
        deviceDTO.setPrice(device.getPrice());
        deviceDTO.setStatus(device.getStatus());
        deviceDTO.setQrcode(device.getQrcode());
        deviceDTO.setImg(device.getImg());
        deviceDTO.setUserManager(device.getUserManager());
        deviceDTO.setCreatedAt(device.getCreatedAt());
        deviceDTO.setUpdatedAt(device.getUpdatedAt());
        deviceDTO.setCreatedBy(device.getCreatedBy());
        deviceDTO.setUpdatedBy(device.getUpdatedBy());
        deviceDTO.setGroup(device.getGroup() == null ? null : device.getGroup());
        deviceDTO.setLine(device.getLine() == null ? null : device.getLine());
        return deviceDTO;
    }

    private Device mapToEntity(final DeviceDTO deviceDTO, final Device device) {
        device.setBracnId(deviceDTO.getBracnId());
        device.setCode(deviceDTO.getCode());
        device.setName(deviceDTO.getName());
        device.setNumMaterialUse(deviceDTO.getNumMaterialUse());
        device.setSerialNumber(deviceDTO.getSerialNumber());
        device.setSource(deviceDTO.getSource());
        device.setInstallationDate(deviceDTO.getInstallationDate());
        device.setMaintenanceCycle(deviceDTO.getMaintenanceCycle());
        device.setDateManufacture(deviceDTO.getDateManufacture());
        device.setUnit(deviceDTO.getUnit());
        device.setPrice(deviceDTO.getPrice());
        device.setStatus(deviceDTO.getStatus());
        device.setQrcode(deviceDTO.getQrcode());
        device.setImg(deviceDTO.getImg());
        device.setUserManager(deviceDTO.getUserManager());
        device.setCreatedAt(deviceDTO.getCreatedAt());
        device.setUpdatedAt(deviceDTO.getUpdatedAt());
        device.setCreatedBy(deviceDTO.getCreatedBy());
        device.setUpdatedBy(deviceDTO.getUpdatedBy());
        final DeviceGroup group = deviceDTO.getGroup() == null ? null : deviceGroupRepository.findById(deviceDTO.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("group not found"));
        device.setGroup(group);
        final Line line = deviceDTO.getLine() == null ? null : lineRepository.findById(deviceDTO.getLine().getId())
                .orElseThrow(() -> new NotFoundException("line not found"));
        device.setLine(line);
        return device;
    }

    public boolean codeExists(final String code) {
        return deviceRepository.existsByCodeIgnoreCase(code);
    }

    @EventListener(BeforeDeleteDeviceGroup.class)
    public void on(final BeforeDeleteDeviceGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final Device groupDevice = deviceRepository.findFirstByGroupId(event.getId());
        if (groupDevice != null) {
            referencedException.setKey("deviceGroup.device.group.referenced");
            referencedException.addParam(groupDevice.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteLine.class)
    public void on(final BeforeDeleteLine event) {
        final ReferencedException referencedException = new ReferencedException();
        final Device lineDevice = deviceRepository.findFirstByLineId(event.getId());
        if (lineDevice != null) {
            referencedException.setKey("line.device.line.referenced");
            referencedException.addParam(lineDevice.getId());
            throw referencedException;
        }
    }

}
