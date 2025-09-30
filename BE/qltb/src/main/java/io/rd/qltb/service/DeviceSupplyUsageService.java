package io.rd.qltb.service;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.DeviceSupplyUsage;
import io.rd.qltb.domain.Supply;
import io.rd.qltb.domain.SupplyDetail;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.events.BeforeDeleteSupply;
import io.rd.qltb.model.DeviceSupplyUsageDTO;
import io.rd.qltb.model.SupplyDetailDTO;
import io.rd.qltb.repos.DeviceRepository;
import io.rd.qltb.repos.DeviceSupplyUsageRepository;
import io.rd.qltb.repos.SupplyRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<DeviceSupplyUsageDTO> getByDeviceId(Long deviceId) {
        return deviceSupplyUsageRepository.findByDeviceId(deviceId).stream()
                .map(s -> mapToDTO(s, new DeviceSupplyUsageDTO()))
                .collect(Collectors.toList());
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

    public List<Long> creates(final List<DeviceSupplyUsageDTO> deviceSupplyUsageDTOS) {
        List<Long> createdIds = new ArrayList<>();
        for (DeviceSupplyUsageDTO dto : deviceSupplyUsageDTOS) {
            DeviceSupplyUsage entity;
            if (dto.getId() != null) {
                entity = deviceSupplyUsageRepository.findById(dto.getId()).orElse(new DeviceSupplyUsage());
            } else {
                entity = new DeviceSupplyUsage();
            }
            mapToEntity(dto, entity);
            DeviceSupplyUsage saved = deviceSupplyUsageRepository.save(entity);
            createdIds.add(saved.getId());
        }
        return createdIds;
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
        deviceSupplyUsageDTO.setDescription(deviceSupplyUsage.getDescription());
        deviceSupplyUsageDTO.setQuantityUsed(deviceSupplyUsage.getQuantityUsed());
        deviceSupplyUsageDTO.setStatus(deviceSupplyUsage.getStatus());

        // Sao chép Device có kiểm soát
        if (deviceSupplyUsage.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(deviceSupplyUsage.getDevice().getId());
            deviceCopy.setCode(deviceSupplyUsage.getDevice().getCode());
            deviceCopy.setName(deviceSupplyUsage.getDevice().getName());
            deviceCopy.setSerialNumber(deviceSupplyUsage.getDevice().getSerialNumber());
            deviceCopy.setUnit(deviceSupplyUsage.getDevice().getUnit());
            deviceCopy.setStatus(deviceSupplyUsage.getDevice().getStatus());
            deviceCopy.setCreatedAt(deviceSupplyUsage.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(deviceSupplyUsage.getDevice().getUpdatedAt());

            // Xóa các quan hệ con
            deviceCopy.setGroup(null);
            deviceCopy.setLine(null);
            deviceCopy.setBranch(null);
            deviceCopy.setTeam(null);
            deviceCopy.setDeviceDeviceParameterUses(null);
            deviceCopy.setDeviceDeviceRelocationHistories(null);
            deviceCopy.setDeviceDeviceSupplyUsages(null);
            deviceCopy.setDevicePlanDetails(null);

            deviceSupplyUsageDTO.setDevice(deviceCopy);
        } else {
            deviceSupplyUsageDTO.setDevice(null);
        }

        // Sao chép Supply có kiểm soát
        if (deviceSupplyUsage.getSupply() != null) {
            Supply supplyCopy = new Supply();
            supplyCopy.setId(deviceSupplyUsage.getSupply().getId());
            supplyCopy.setCode(deviceSupplyUsage.getSupply().getCode());
            supplyCopy.setName(deviceSupplyUsage.getSupply().getName());
            supplyCopy.setStatus(deviceSupplyUsage.getSupply().getStatus());
            supplyCopy.setCreatedAt(deviceSupplyUsage.getSupply().getCreatedAt());
            supplyCopy.setUpdatedAt(deviceSupplyUsage.getSupply().getUpdatedAt());

            // Xóa các quan hệ con
            supplyCopy.setGroup(null);
            supplyCopy.setSupplySupplyDetails(null);
            supplyCopy.setSupplyDeviceSupplyUsages(null);

            deviceSupplyUsageDTO.setSupply(supplyCopy);
        } else {
            deviceSupplyUsageDTO.setSupply(null);
        }

        return deviceSupplyUsageDTO;
    }


    private DeviceSupplyUsage mapToEntity(final DeviceSupplyUsageDTO deviceSupplyUsageDTO,
            final DeviceSupplyUsage deviceSupplyUsage) {
        deviceSupplyUsage.setUsageDate(deviceSupplyUsageDTO.getUsageDate());
        deviceSupplyUsage.setSerial(deviceSupplyUsageDTO.getSerial());
        deviceSupplyUsage.setDescription(deviceSupplyUsageDTO.getDescription());
        deviceSupplyUsage.setQuantityUsed(deviceSupplyUsageDTO.getQuantityUsed());
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
