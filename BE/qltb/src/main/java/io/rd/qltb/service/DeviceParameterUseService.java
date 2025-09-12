package io.rd.qltb.service;


import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.DeviceParameterUse;
import io.rd.qltb.domain.Prameter;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.events.BeforeDeletePrameter;
import io.rd.qltb.model.DeviceParameterUseDTO;
import io.rd.qltb.repos.DeviceParameterUseRepository;
import io.rd.qltb.repos.DeviceRepository;
import io.rd.qltb.repos.PrameterRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


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
        final List<DeviceParameterUse> deviceParameterUses = deviceParameterUseRepository.findAll(Sort.by("id"));
        return deviceParameterUses.stream()
                .map(deviceParameterUse -> mapToDTO(deviceParameterUse, new DeviceParameterUseDTO()))
                .toList();
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
            final DeviceParameterUseDTO deviceParameterUseDTO) {
        deviceParameterUseDTO.setId(deviceParameterUse.getId());
        deviceParameterUseDTO.setValue(deviceParameterUse.getValue());
        deviceParameterUseDTO.setMin(deviceParameterUse.getMin());
        deviceParameterUseDTO.setMax(deviceParameterUse.getMax());
        deviceParameterUseDTO.setUnit(deviceParameterUse.getUnit());
        deviceParameterUseDTO.setDescription(deviceParameterUse.getDescription());
        deviceParameterUseDTO.setCreatedAt(deviceParameterUse.getCreatedAt());
        deviceParameterUseDTO.setUpdatedAt(deviceParameterUse.getUpdatedAt());
        deviceParameterUseDTO.setCreatedBy(deviceParameterUse.getCreatedBy());
        deviceParameterUseDTO.setUpdatedBy(deviceParameterUse.getUpdatedBy());
        deviceParameterUseDTO.setStatus(deviceParameterUse.getStatus());
        deviceParameterUseDTO.setDevice(deviceParameterUse.getDevice() == null ? null : deviceParameterUse.getDevice());
        deviceParameterUseDTO.setParameter(deviceParameterUse.getParameter() == null ? null : deviceParameterUse.getParameter());
        return deviceParameterUseDTO;
    }

    private DeviceParameterUse mapToEntity(final DeviceParameterUseDTO deviceParameterUseDTO,
            final DeviceParameterUse deviceParameterUse) {
        deviceParameterUse.setValue(deviceParameterUseDTO.getValue());
        deviceParameterUse.setMin(deviceParameterUseDTO.getMin());
        deviceParameterUse.setMax(deviceParameterUseDTO.getMax());
        deviceParameterUse.setUnit(deviceParameterUseDTO.getUnit());
        deviceParameterUse.setDescription(deviceParameterUseDTO.getDescription());
        deviceParameterUse.setCreatedAt(deviceParameterUseDTO.getCreatedAt());
        deviceParameterUse.setUpdatedAt(deviceParameterUseDTO.getUpdatedAt());
        deviceParameterUse.setCreatedBy(deviceParameterUseDTO.getCreatedBy());
        deviceParameterUse.setUpdatedBy(deviceParameterUseDTO.getUpdatedBy());
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
