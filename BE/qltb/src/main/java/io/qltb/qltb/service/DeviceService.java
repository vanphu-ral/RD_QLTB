package io.qltb.qltb.service;

import io.qltb.qltb.domain.Branch;
import io.qltb.qltb.domain.Device;
import io.qltb.qltb.domain.DeviceGroup;
import io.qltb.qltb.domain.Line;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.events.BeforeDeleteDeviceGroup;
import io.qltb.qltb.events.BeforeDeleteLine;
import io.qltb.qltb.model.DeviceDTO;
import io.qltb.qltb.repos.BranchRepository;
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
    private final BranchRepository branchRepository;
    private final ApplicationEventPublisher publisher;

    public DeviceService(final DeviceRepository deviceRepository,
            final DeviceGroupRepository deviceGroupRepository, final LineRepository lineRepository, final  BranchRepository branchRepository,
            final ApplicationEventPublisher publisher) {
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.lineRepository = lineRepository;
        this.branchRepository = branchRepository;
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
        deviceDTO.setId(device.getId());
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
        deviceDTO.setDescription(device.getDescription());
        deviceDTO.setCreatedAt(device.getCreatedAt());
        deviceDTO.setUpdatedAt(device.getUpdatedAt());
        deviceDTO.setCreatedBy(device.getCreatedBy());
        deviceDTO.setUpdatedBy(device.getUpdatedBy());

        if(device.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(device.getBranch().getId());
            branchCopy.setCode(device.getBranch().getCode());
            branchCopy.setName(device.getBranch().getName());
            // Xóa các quan hệ con
            branchCopy.setFactory(null);
            branchCopy.setBranchDayOffs(null);
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchForms(null);
            branchCopy.setBranchPlanTargets(null);
        } else {
            deviceDTO.setBranch(null);
        }

        if (device.getGroup() != null) {
            DeviceGroup groupCopy = new DeviceGroup();
            groupCopy.setId(device.getGroup().getId());
            groupCopy.setCode(device.getGroup().getCode());
            groupCopy.setName(device.getGroup().getName());
            groupCopy.setStatus(device.getGroup().getStatus());
            groupCopy.setCreatedAt(device.getGroup().getCreatedAt());
            groupCopy.setUpdatedAt(device.getGroup().getUpdatedAt());
            groupCopy.setCreatedBy(device.getGroup().getCreatedBy());
            groupCopy.setUpdatedBy(device.getGroup().getUpdatedBy());

            // Xóa các quan hệ con
            groupCopy.setGroupDevices(null);
            groupCopy.setDeviceGroupSampleReports(null);
            groupCopy.setDeviceGroupPlanDetails(null);
            groupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);

            deviceDTO.setGroup(groupCopy);
        } else {
            deviceDTO.setGroup(null);
        }

        if (device.getLine() != null) {
            Line lineCopy = new Line();
            lineCopy.setId(device.getLine().getId());
            lineCopy.setCode(device.getLine().getCode());
            lineCopy.setName(device.getLine().getName());
            lineCopy.setStatus(device.getLine().getStatus());
            lineCopy.setCreatedAt(device.getLine().getCreatedAt());
            lineCopy.setUpdatedAt(device.getLine().getUpdatedAt());
            lineCopy.setCreatedBy(device.getLine().getCreatedBy());
            lineCopy.setUpdatedBy(device.getLine().getUpdatedBy());

            // Xóa các quan hệ con
            lineCopy.setTeam(null);
            lineCopy.setLineDevices(null);
            lineCopy.setLineForms(null);

            deviceDTO.setLine(lineCopy);
        } else {
            deviceDTO.setLine(null);
        }

        return deviceDTO;
    }


    private Device mapToEntity(final DeviceDTO deviceDTO, final Device device) {
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
        device.setDescription(deviceDTO.getDescription());
        device.setCreatedAt(deviceDTO.getCreatedAt());
        device.setUpdatedAt(deviceDTO.getUpdatedAt());
        device.setCreatedBy(deviceDTO.getCreatedBy());
        device.setUpdatedBy(deviceDTO.getUpdatedBy());
        final DeviceGroup group = deviceDTO.getGroup() == null ? null : deviceGroupRepository.findById(deviceDTO.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("group not found"));
        device.setGroup(group);
        final Branch branch = deviceDTO.getBranch() == null ? null : branchRepository.findById(deviceDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        device.setBranch(branch);
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
