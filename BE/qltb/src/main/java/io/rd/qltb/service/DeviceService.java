package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeleteBranch;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.events.BeforeDeleteLine;
import io.rd.qltb.events.BeforeDeleteTeam;
import io.rd.qltb.model.DeviceDTO;
import io.rd.qltb.model.DeviceSupplyUsageDTO;
import io.rd.qltb.repos.BranchRepository;
import io.rd.qltb.repos.DeviceGroupRepository;
import io.rd.qltb.repos.DeviceRepository;
import io.rd.qltb.repos.LineRepository;
import io.rd.qltb.repos.TeamRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.util.ArrayList;
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
    private final TeamRepository teamRepository;
    private final ApplicationEventPublisher publisher;

    public DeviceService(final DeviceRepository deviceRepository,
            final DeviceGroupRepository deviceGroupRepository, final LineRepository lineRepository,
            final BranchRepository branchRepository, final TeamRepository teamRepository,
            final ApplicationEventPublisher publisher) {
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.lineRepository = lineRepository;
        this.branchRepository = branchRepository;
        this.teamRepository = teamRepository;
        this.publisher = publisher;
    }

    public List<DeviceDTO> findAll() {
        final List<Device> devices = deviceRepository.findAll(Sort.by("id"));
        return devices.stream()
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .toList();
    }

    public List<DeviceDTO> getDevicesByGroupId(Long groupId) {
        final List<Device> devices = deviceRepository.findByGroupId(groupId);
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
    public List<Long> creates(final List<DeviceDTO> deviceDTO) {
        List<Long> createdIds = new ArrayList<>();
        for (DeviceDTO dto : deviceDTO) {
            Device entity;
            if (dto.getId() != null) {
                entity = deviceRepository.findById(dto.getId()).orElse(new Device());
            } else {
                entity = new Device();
            }
            mapToEntity(dto, entity);
            Device saved = deviceRepository.save(entity);
            createdIds.add(saved.getId());
        }
        return createdIds;
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

    public DeviceDTO mapToDTO(final Device device, final DeviceDTO deviceDTO) {
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
        deviceDTO.setSupplier(device.getSupplier());

        // Sao chép DeviceGroup có kiểm soát
        if (device.getGroup() != null) {
            DeviceGroup groupCopy = new DeviceGroup();
            groupCopy.setId(device.getGroup().getId());
            groupCopy.setCode(device.getGroup().getCode());
            groupCopy.setName(device.getGroup().getName());
            groupCopy.setDescription(device.getGroup().getDescription());
            groupCopy.setCreatedAt(device.getGroup().getCreatedAt());
            groupCopy.setUpdatedAt(device.getGroup().getUpdatedAt());
            groupCopy.setCreatedBy(device.getGroup().getCreatedBy());
            groupCopy.setUpdatedBy(device.getGroup().getUpdatedBy());
            groupCopy.setStatus(device.getGroup().getStatus());

            // Xóa các quan hệ con
            groupCopy.setDeviceGroupSampleReports(null);
            groupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);
            groupCopy.setGroupDevices(null);
            groupCopy.setDeviceGroupPlanDetails(null);

            deviceDTO.setGroup(groupCopy);
        } else {
            deviceDTO.setGroup(null);
        }

        // Sao chép Line có kiểm soát
        if (device.getLine() != null) {
            Line lineCopy = new Line();
            lineCopy.setId(device.getLine().getId());
            lineCopy.setCode(device.getLine().getCode());
            lineCopy.setName(device.getLine().getName());
            lineCopy.setDescription(device.getLine().getDescription());
            lineCopy.setManager(device.getLine().getManager());
            lineCopy.setCreatedAt(device.getLine().getCreatedAt());
            lineCopy.setUpdatedAt(device.getLine().getUpdatedAt());
            lineCopy.setCreatedBy(device.getLine().getCreatedBy());
            lineCopy.setUpdatedBy(device.getLine().getUpdatedBy());
            lineCopy.setStatus(device.getLine().getStatus());

            // Xóa các quan hệ con
            lineCopy.setTeam(null);
            lineCopy.setLineDevices(null);

            deviceDTO.setLine(lineCopy);
        } else {
            deviceDTO.setLine(null);
        }

        // Sao chép Branch có kiểm soát
        if (device.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(device.getBranch().getId());
            branchCopy.setCode(device.getBranch().getCode());
            branchCopy.setName(device.getBranch().getName());
            branchCopy.setDescription(device.getBranch().getDescription());
            branchCopy.setManager(device.getBranch().getManager());
            branchCopy.setCreatedAt(device.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(device.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(device.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(device.getBranch().getUpdatedBy());
            branchCopy.setStatus(device.getBranch().getStatus());

            // Xóa các quan hệ con
            branchCopy.setFactory(null);
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);

            deviceDTO.setBranch(branchCopy);
        } else {
            deviceDTO.setBranch(null);
        }

        // Sao chép Team có kiểm soát
        if (device.getTeam() != null) {
            Team teamCopy = new Team();
            teamCopy.setId(device.getTeam().getId());
            teamCopy.setCode(device.getTeam().getCode());
            teamCopy.setName(device.getTeam().getName());
            teamCopy.setDescription(device.getTeam().getDescription());
            teamCopy.setManager(device.getTeam().getManager());
            teamCopy.setCreatedAt(device.getTeam().getCreatedAt());
            teamCopy.setUpdatedAt(device.getTeam().getUpdatedAt());
            teamCopy.setCreatedBy(device.getTeam().getCreatedBy());
            teamCopy.setUpdatedBy(device.getTeam().getUpdatedBy());
            teamCopy.setStatus(device.getTeam().getStatus());

            // Xóa các quan hệ con
            teamCopy.setBranch(null);
            teamCopy.setTeamLines(null);
            teamCopy.setTeamDevices(null);
            deviceDTO.setTeam(teamCopy);
        } else {
            deviceDTO.setTeam(null);
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
        device.setSupplier(deviceDTO.getSupplier());
        final DeviceGroup group = deviceDTO.getGroup() == null ? null : deviceGroupRepository.findById(deviceDTO.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("group not found"));
        device.setGroup(group);
        final Line line = deviceDTO.getLine() == null ? null : lineRepository.findById(deviceDTO.getLine().getId())
                .orElseThrow(() -> new NotFoundException("line not found"));
        device.setLine(line);
        final Branch branch = deviceDTO.getBranch() == null ? null : branchRepository.findById(deviceDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        device.setBranch(branch);
        final Team team = deviceDTO.getTeam() == null ? null : teamRepository.findById(deviceDTO.getTeam().getId())
                .orElseThrow(() -> new NotFoundException("team not found"));
        device.setTeam(team);
        return device;
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

    @EventListener(BeforeDeleteBranch.class)
    public void on(final BeforeDeleteBranch event) {
        final ReferencedException referencedException = new ReferencedException();
        final Device branchDevice = deviceRepository.findFirstByBranchId(event.getId());
        if (branchDevice != null) {
            referencedException.setKey("branch.device.branch.referenced");
            referencedException.addParam(branchDevice.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final Device teamDevice = deviceRepository.findFirstByTeamId(event.getId());
        if (teamDevice != null) {
            referencedException.setKey("team.device.team.referenced");
            referencedException.addParam(teamDevice.getId());
            throw referencedException;
        }
    }

}
