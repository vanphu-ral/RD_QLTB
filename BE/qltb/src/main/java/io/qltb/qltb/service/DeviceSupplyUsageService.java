package io.qltb.qltb.service;

import io.qltb.qltb.domain.Device;
import io.qltb.qltb.domain.DeviceSupplyUsage;
import io.qltb.qltb.domain.Supply;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.events.BeforeDeleteSupply;
import io.qltb.qltb.model.DeviceSupplyUsageDTO;
import io.qltb.qltb.repos.DeviceRepository;
import io.qltb.qltb.repos.DeviceSupplyUsageRepository;
import io.qltb.qltb.repos.SupplyRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DeviceSupplyUsageService {

    private final DeviceSupplyUsageRepository deviceSupplyUsageRepository;
    private final DeviceRepository deviceRepository;
    private final SupplyRepository supplyRepository;

    public DeviceSupplyUsageService(final DeviceSupplyUsageRepository deviceSupplyUsageRepository,
            final DeviceRepository deviceRepository, final SupplyRepository supplyRepository) {
        this.deviceSupplyUsageRepository = deviceSupplyUsageRepository;
        this.deviceRepository = deviceRepository;
        this.supplyRepository = supplyRepository;
    }

    public List<DeviceSupplyUsageDTO> findAll() {
        final List<DeviceSupplyUsage> deviceSupplyUsages = deviceSupplyUsageRepository.findAll(Sort.by("id"));
        return deviceSupplyUsages.stream()
                .map(deviceSupplyUsage -> mapToDTO(deviceSupplyUsage, new DeviceSupplyUsageDTO()))
                .toList();
    }

    public DeviceSupplyUsageDTO get(final Long id) {
        return deviceSupplyUsageRepository.findById(id)
                .map(deviceSupplyUsage -> mapToDTO(deviceSupplyUsage, new DeviceSupplyUsageDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceSupplyUsageDTO deviceSupplyUsageDTO) {
        final DeviceSupplyUsage deviceSupplyUsage = new DeviceSupplyUsage();
        mapToEntity(deviceSupplyUsageDTO, deviceSupplyUsage);
        return deviceSupplyUsageRepository.save(deviceSupplyUsage).getId();
    }

    public void update(final Long id, final DeviceSupplyUsageDTO deviceSupplyUsageDTO) {
        final DeviceSupplyUsage deviceSupplyUsage = deviceSupplyUsageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceSupplyUsageDTO, deviceSupplyUsage);
        deviceSupplyUsageRepository.save(deviceSupplyUsage);
    }

    public void delete(final Long id) {
        final DeviceSupplyUsage deviceSupplyUsage = deviceSupplyUsageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        deviceSupplyUsageRepository.delete(deviceSupplyUsage);
    }

    private DeviceSupplyUsageDTO mapToDTO(final DeviceSupplyUsage deviceSupplyUsage,
            final DeviceSupplyUsageDTO deviceSupplyUsageDTO) {
        deviceSupplyUsageDTO.setId(deviceSupplyUsage.getId());
        deviceSupplyUsageDTO.setUsageDate(deviceSupplyUsage.getUsageDate());
        deviceSupplyUsageDTO.setSerial(deviceSupplyUsage.getSerial());
        deviceSupplyUsageDTO.setNote(deviceSupplyUsage.getNote());
        deviceSupplyUsageDTO.setCreatedAt(deviceSupplyUsage.getCreatedAt());
        deviceSupplyUsageDTO.setUpdatedAt(deviceSupplyUsage.getUpdatedAt());
        deviceSupplyUsageDTO.setCreatedBy(deviceSupplyUsage.getCreatedBy());
        deviceSupplyUsageDTO.setUpdatedBy(deviceSupplyUsage.getUpdatedBy());
        deviceSupplyUsageDTO.setStatus(deviceSupplyUsage.getStatus());
        deviceSupplyUsageDTO.setDevice(deviceSupplyUsage.getDevice() == null ? null : deviceSupplyUsage.getDevice());
        deviceSupplyUsageDTO.setSupply(deviceSupplyUsage.getSupply() == null ? null : deviceSupplyUsage.getSupply());
        return deviceSupplyUsageDTO;
    }

    private DeviceSupplyUsage mapToEntity(final DeviceSupplyUsageDTO deviceSupplyUsageDTO,
            final DeviceSupplyUsage deviceSupplyUsage) {
        deviceSupplyUsage.setUsageDate(deviceSupplyUsageDTO.getUsageDate());
        deviceSupplyUsage.setSerial(deviceSupplyUsageDTO.getSerial());
        deviceSupplyUsage.setNote(deviceSupplyUsageDTO.getNote());
        deviceSupplyUsage.setCreatedAt(deviceSupplyUsageDTO.getCreatedAt());
        deviceSupplyUsage.setUpdatedAt(deviceSupplyUsageDTO.getUpdatedAt());
        deviceSupplyUsage.setCreatedBy(deviceSupplyUsageDTO.getCreatedBy());
        deviceSupplyUsage.setUpdatedBy(deviceSupplyUsageDTO.getUpdatedBy());
        deviceSupplyUsage.setStatus(deviceSupplyUsageDTO.getStatus());
        final Device device = deviceSupplyUsageDTO.getDevice() == null ? null : deviceRepository.findById(deviceSupplyUsageDTO.getDevice().getId())
                .orElseThrow(() -> new NotFoundException("device not found"));
        deviceSupplyUsage.setDevice(device);
        final Supply supply = deviceSupplyUsageDTO.getSupply() == null ? null : supplyRepository.findById(deviceSupplyUsageDTO.getSupply().getId())
                .orElseThrow(() -> new NotFoundException("supply not found"));
        deviceSupplyUsage.setSupply(supply);
        return deviceSupplyUsage;
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final DeviceSupplyUsage deviceDeviceSupplyUsage = deviceSupplyUsageRepository.findFirstByDeviceId(event.getId());
        if (deviceDeviceSupplyUsage != null) {
            referencedException.setKey("device.deviceSupplyUsage.device.referenced");
            referencedException.addParam(deviceDeviceSupplyUsage.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteSupply.class)
    public void on(final BeforeDeleteSupply event) {
        final ReferencedException referencedException = new ReferencedException();
        final DeviceSupplyUsage supplyDeviceSupplyUsage = deviceSupplyUsageRepository.findFirstBySupplyId(event.getId());
        if (supplyDeviceSupplyUsage != null) {
            referencedException.setKey("supply.deviceSupplyUsage.supply.referenced");
            referencedException.addParam(supplyDeviceSupplyUsage.getId());
            throw referencedException;
        }
    }

}
