package io.rd.qltb.service;


import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.DeviceParameterUse;
import io.rd.qltb.domain.Prameter;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.events.BeforeDeletePrameter;
import io.rd.qltb.model.DeviceParameterUseDTO;
import io.rd.qltb.model.DeviceSupplyUsageDTO;
import io.rd.qltb.model.PrameterDTO;
import io.rd.qltb.repos.DeviceParameterUseRepository;
import io.rd.qltb.repos.DeviceRepository;
import io.rd.qltb.repos.PrameterRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class DeviceParameterUseService {

    private final DeviceParameterUseRepository deviceParameterUseRepository;
    private final DeviceRepository deviceRepository;
    private final PrameterRepository prameterRepository;

    public DeviceParameterUseService(
            final DeviceParameterUseRepository deviceParameterUseRepository,
            final DeviceRepository deviceRepository, final PrameterRepository prameterRepository) {
        this.deviceParameterUseRepository = deviceParameterUseRepository;
        this.deviceRepository = deviceRepository;
        this.prameterRepository = prameterRepository;
    }

    public List<DeviceParameterUseDTO> findAll() {
        final List<DeviceParameterUse> deviceParameterUses = deviceParameterUseRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
        return deviceParameterUses.stream()
                .map(deviceParameterUse -> mapToDTO(deviceParameterUse, new DeviceParameterUseDTO()))
                .toList();
    }

    public List<DeviceParameterUseDTO> getByDeviceId(Long deviceId) {
        return deviceParameterUseRepository.findByDeviceId(deviceId).stream()
                .map(s -> mapToDTO(s, new DeviceParameterUseDTO()))
                .collect(Collectors.toList());
    }

    public DeviceParameterUseDTO get(final Long id) {
        return deviceParameterUseRepository.findById(id)
                .map(deviceParameterUse -> mapToDTO(deviceParameterUse, new DeviceParameterUseDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceParameterUseDTO deviceParameterUseDTO) {
        final DeviceParameterUse deviceParameterUse = new DeviceParameterUse();
        mapToEntity(deviceParameterUseDTO, deviceParameterUse);
        return deviceParameterUseRepository.save(deviceParameterUse).getId();
    }
    public List<Long> creates(final List<DeviceParameterUseDTO> deviceParameterUseDTOS) {
        List<Long> createdIds = new ArrayList<>();
        for (DeviceParameterUseDTO dto : deviceParameterUseDTOS) {
            DeviceParameterUse entity;
            if (dto.getId() != null) {
                entity = deviceParameterUseRepository.findById(dto.getId()).orElse(new DeviceParameterUse());
            } else {
                entity = new DeviceParameterUse();
            }
            mapToEntity(dto, entity);
            DeviceParameterUse saved = deviceParameterUseRepository.save(entity);
            createdIds.add(saved.getId());
        }
        return createdIds;
    }
    public void update(final Long id, final DeviceParameterUseDTO deviceParameterUseDTO) {
        final DeviceParameterUse deviceParameterUse = deviceParameterUseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceParameterUseDTO, deviceParameterUse);
        deviceParameterUseRepository.save(deviceParameterUse);
    }

    public void delete(final Long id) {
        final DeviceParameterUse deviceParameterUse = deviceParameterUseRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        deviceParameterUseRepository.delete(deviceParameterUse);
    }
    private DeviceParameterUseDTO mapToDTO(final DeviceParameterUse deviceParameterUse,
                                           final DeviceParameterUseDTO dto) {
        dto.setId(deviceParameterUse.getId());
        dto.setValue(deviceParameterUse.getValue());
        dto.setMin(deviceParameterUse.getMin());
        dto.setMax(deviceParameterUse.getMax());
        dto.setUnit(deviceParameterUse.getUnit());
        dto.setDescription(deviceParameterUse.getDescription());
        dto.setStatus(deviceParameterUse.getStatus());

        // Sao chép Device có kiểm soát
        if (deviceParameterUse.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(deviceParameterUse.getDevice().getId());
            deviceCopy.setCode(deviceParameterUse.getDevice().getCode());
            deviceCopy.setName(deviceParameterUse.getDevice().getName());
            deviceCopy.setSerialNumber(deviceParameterUse.getDevice().getSerialNumber());
            deviceCopy.setUnit(deviceParameterUse.getDevice().getUnit());
            deviceCopy.setStatus(deviceParameterUse.getDevice().getStatus());
            deviceCopy.setCreatedAt(deviceParameterUse.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(deviceParameterUse.getDevice().getUpdatedAt());

            // Xóa các quan hệ con để tránh vòng lặp
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

        // Sao chép Prameter có kiểm soát
        if (deviceParameterUse.getParameter() != null) {
            Prameter paramCopy = new Prameter();
            paramCopy.setId(deviceParameterUse.getParameter().getId());
            paramCopy.setCode(deviceParameterUse.getParameter().getCode());
            paramCopy.setName(deviceParameterUse.getParameter().getName());
            paramCopy.setDescription(deviceParameterUse.getParameter().getDescription());
            paramCopy.setCreatedAt(deviceParameterUse.getParameter().getCreatedAt());
            paramCopy.setUpdatedAt(deviceParameterUse.getParameter().getUpdatedAt());
            paramCopy.setCreatedBy(deviceParameterUse.getParameter().getCreatedBy());
            paramCopy.setUpdatedBy(deviceParameterUse.getParameter().getUpdatedBy());
            paramCopy.setStatus(deviceParameterUse.getParameter().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            paramCopy.setParameterGroup(null);
            dto.setParameter(paramCopy);
        } else {
            dto.setParameter(null);
        }

        return dto;
    }
    private DeviceParameterUse mapToEntity(final DeviceParameterUseDTO deviceParameterUseDTO,
                                           final DeviceParameterUse deviceParameterUse) {
        deviceParameterUse.setValue(deviceParameterUseDTO.getValue());
        deviceParameterUse.setMin(deviceParameterUseDTO.getMin());
        deviceParameterUse.setMax(deviceParameterUseDTO.getMax());
        deviceParameterUse.setUnit(deviceParameterUseDTO.getUnit());
        deviceParameterUse.setDescription(deviceParameterUseDTO.getDescription());
        deviceParameterUse.setStatus(deviceParameterUseDTO.getStatus());
        final Device device = deviceParameterUseDTO.getDevice() == null ? null : deviceRepository.findById(deviceParameterUseDTO.getDevice().getId())
                .orElseThrow(() -> new NotFoundException("device not found"));
        deviceParameterUse.setDevice(device);
        final Prameter parameter = deviceParameterUseDTO.getParameter() == null ? null : prameterRepository.findById(deviceParameterUseDTO.getParameter().getId())
                .orElseThrow(() -> new NotFoundException("parameter not found"));
        deviceParameterUse.setParameter(parameter);
        return deviceParameterUse;
    }
    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final DeviceParameterUse deviceDeviceParameterUse = deviceParameterUseRepository.findFirstByDeviceId(event.getId());
        if (deviceDeviceParameterUse != null) {
            referencedException.setKey("device.deviceParameterUse.device.referenced");
            referencedException.addParam(deviceDeviceParameterUse.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeletePrameter.class)
    public void on(final BeforeDeletePrameter event) {
        final ReferencedException referencedException = new ReferencedException();
        final DeviceParameterUse parameterDeviceParameterUse = deviceParameterUseRepository.findFirstByParameterId(event.getId());
        if (parameterDeviceParameterUse != null) {
            referencedException.setKey("prameter.deviceParameterUse.parameter.referenced");
            referencedException.addParam(parameterDeviceParameterUse.getId());
            throw referencedException;
        }
    }

}
