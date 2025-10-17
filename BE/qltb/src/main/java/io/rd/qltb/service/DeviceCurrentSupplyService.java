package io.rd.qltb.service;


import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.DeviceCurrentSupply;
import io.rd.qltb.domain.DeviceSupplyUsage;
import io.rd.qltb.domain.SupplyDetail;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.events.BeforeDeleteSupplyDetail;
import io.rd.qltb.model.DeviceCurrentSupplyDTO;
import io.rd.qltb.model.DeviceSupplyUsageDTO;
import io.rd.qltb.repos.DeviceCurrentSupplyRepository;
import io.rd.qltb.repos.DeviceRepository;
import io.rd.qltb.repos.SupplyDetailRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DeviceCurrentSupplyService {

    private final DeviceCurrentSupplyRepository deviceCurrentSupplyRepository;
    private final DeviceRepository deviceRepository;
    private final SupplyDetailRepository supplyDetailRepository;

    public DeviceCurrentSupplyService(
            final DeviceCurrentSupplyRepository deviceCurrentSupplyRepository,
            final DeviceRepository deviceRepository,
            final SupplyDetailRepository supplyDetailRepository) {
        this.deviceCurrentSupplyRepository = deviceCurrentSupplyRepository;
        this.deviceRepository = deviceRepository;
        this.supplyDetailRepository = supplyDetailRepository;
    }

    public List<DeviceCurrentSupplyDTO> findAll() {
        final List<DeviceCurrentSupply> deviceCurrentSupplies = deviceCurrentSupplyRepository.findAll(Sort.by("id"));
        return deviceCurrentSupplies.stream()
                .map(deviceCurrentSupply -> mapToDTO(deviceCurrentSupply, new DeviceCurrentSupplyDTO()))
                .toList();
    }

    public DeviceCurrentSupplyDTO get(final Long id) {
        return deviceCurrentSupplyRepository.findById(id)
                .map(deviceCurrentSupply -> mapToDTO(deviceCurrentSupply, new DeviceCurrentSupplyDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceCurrentSupplyDTO deviceCurrentSupplyDTO) {
        final DeviceCurrentSupply deviceCurrentSupply = new DeviceCurrentSupply();
        mapToEntity(deviceCurrentSupplyDTO, deviceCurrentSupply);
        return deviceCurrentSupplyRepository.save(deviceCurrentSupply).getId();
    }
    public List<Long> creates(final List<DeviceCurrentSupplyDTO> deviceSupplyUsageDTOS) {
        List<Long> createdIds = new ArrayList<>();
        for (DeviceCurrentSupplyDTO dto : deviceSupplyUsageDTOS) {
            DeviceCurrentSupply entity;
            if (dto.getId() != null) {
                entity = deviceCurrentSupplyRepository.findById(dto.getId()).orElse(new DeviceCurrentSupply());
            } else {
                entity = new DeviceCurrentSupply();
            }
            mapToEntity(dto, entity);
            DeviceCurrentSupply saved = deviceCurrentSupplyRepository.save(entity);
            createdIds.add(saved.getId());
        }
        return createdIds;
    }
    public void update(final Long id, final DeviceCurrentSupplyDTO deviceCurrentSupplyDTO) {
        final DeviceCurrentSupply deviceCurrentSupply = deviceCurrentSupplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceCurrentSupplyDTO, deviceCurrentSupply);
        deviceCurrentSupplyRepository.save(deviceCurrentSupply);
    }

    public void delete(final Long id) {
        final DeviceCurrentSupply deviceCurrentSupply = deviceCurrentSupplyRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        deviceCurrentSupplyRepository.delete(deviceCurrentSupply);
    }

    public DeviceCurrentSupplyDTO mapToDTO(final DeviceCurrentSupply deviceCurrentSupply,
            final DeviceCurrentSupplyDTO deviceCurrentSupplyDTO) {
        deviceCurrentSupplyDTO.setId(deviceCurrentSupply.getId());
        deviceCurrentSupplyDTO.setQuantity(deviceCurrentSupply.getQuantity());
        deviceCurrentSupplyDTO.setStatus(deviceCurrentSupply.getStatus());
        deviceCurrentSupplyDTO.setLastReplacementDate(deviceCurrentSupply.getLastReplacementDate());
        deviceCurrentSupplyDTO.setDevice(deviceCurrentSupply.getDevice() == null ? null : deviceCurrentSupply.getDevice());
        deviceCurrentSupplyDTO.setSupplyDetail(deviceCurrentSupply.getSupplyDetail() == null ? null : deviceCurrentSupply.getSupplyDetail());
        // Sao chép Device có kiểm soát
        if (deviceCurrentSupply.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(deviceCurrentSupply.getDevice().getId());
            deviceCopy.setCode(deviceCurrentSupply.getDevice().getCode());
            deviceCopy.setName(deviceCurrentSupply.getDevice().getName());
            deviceCopy.setSerialNumber(deviceCurrentSupply.getDevice().getSerialNumber());
            deviceCopy.setUnit(deviceCurrentSupply.getDevice().getUnit());
            deviceCopy.setStatus(deviceCurrentSupply.getDevice().getStatus());
            deviceCopy.setCreatedAt(deviceCurrentSupply.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(deviceCurrentSupply.getDevice().getUpdatedAt());

            // Xóa các quan hệ con
            deviceCopy.setGroup(null);
            deviceCopy.setLine(null);
            deviceCopy.setBranch(null);
            deviceCopy.setTeam(null);
            deviceCopy.setDeviceDeviceParameterUses(null);
            deviceCopy.setDeviceDeviceRelocationHistories(null);
            deviceCopy.setDeviceDeviceSupplyUsages(null);
            deviceCopy.setDevicePlanDetails(null);

            deviceCurrentSupplyDTO.setDevice(deviceCopy);
        } else {
            deviceCurrentSupplyDTO.setDevice(null);
        }
        // Sao chép SupplyDetail có kiểm soát
        if (deviceCurrentSupply.getSupplyDetail() != null) {
            SupplyDetail supplyDetailCopy = new SupplyDetail();
            supplyDetailCopy.setId(deviceCurrentSupply.getSupplyDetail().getId());
            supplyDetailCopy.setSerial(deviceCurrentSupply.getSupplyDetail().getSerial());
            supplyDetailCopy.setImportDate(deviceCurrentSupply.getSupplyDetail().getImportDate());
            supplyDetailCopy.setSupplier(deviceCurrentSupply.getSupplyDetail().getSupplier());
            supplyDetailCopy.setPrice(deviceCurrentSupply.getSupplyDetail().getPrice());
            supplyDetailCopy.setUnit(deviceCurrentSupply.getSupplyDetail().getUnit());
            supplyDetailCopy.setCurrency(deviceCurrentSupply.getSupplyDetail().getCurrency());
            supplyDetailCopy.setQuantity(deviceCurrentSupply.getSupplyDetail().getQuantity());
            supplyDetailCopy.setStatus(deviceCurrentSupply.getSupplyDetail().getStatus());

            // Xóa các quan hệ con
            supplyDetailCopy.setSupply(null);

            deviceCurrentSupplyDTO.setSupplyDetail(supplyDetailCopy);
        } else {
            deviceCurrentSupplyDTO.setSupplyDetail(null);
        }
        return deviceCurrentSupplyDTO;
    }

    public DeviceCurrentSupply mapToEntity(final DeviceCurrentSupplyDTO deviceCurrentSupplyDTO,
            final DeviceCurrentSupply deviceCurrentSupply) {
        deviceCurrentSupply.setQuantity(deviceCurrentSupplyDTO.getQuantity());
        deviceCurrentSupply.setStatus(deviceCurrentSupplyDTO.getStatus());
        deviceCurrentSupply.setLastReplacementDate(deviceCurrentSupplyDTO.getLastReplacementDate());
        final Device device = deviceCurrentSupplyDTO.getDevice() == null ? null : deviceRepository.findById(deviceCurrentSupplyDTO.getDevice().getId())
                .orElseThrow(() -> new NotFoundException("device not found"));
        deviceCurrentSupply.setDevice(device);
        final SupplyDetail supplyDetail = deviceCurrentSupplyDTO.getSupplyDetail() == null ? null : supplyDetailRepository.findById(deviceCurrentSupplyDTO.getSupplyDetail().getId())
                .orElseThrow(() -> new NotFoundException("supplyDetail not found"));
        deviceCurrentSupply.setSupplyDetail(supplyDetail);
        return deviceCurrentSupply;
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final DeviceCurrentSupply deviceDeviceCurrentSupply = deviceCurrentSupplyRepository.findFirstByDeviceId(Math.toIntExact(event.getId()));
        if (deviceDeviceCurrentSupply != null) {
            referencedException.setKey("device.deviceCurrentSupply.device.referenced");
            referencedException.addParam(deviceDeviceCurrentSupply.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteSupplyDetail.class)
    public void on(final BeforeDeleteSupplyDetail event) {
        final ReferencedException referencedException = new ReferencedException();
        final DeviceCurrentSupply supplyDetailDeviceCurrentSupply = deviceCurrentSupplyRepository.findFirstBySupplyDetailId(event.getId());
        if (supplyDetailDeviceCurrentSupply != null) {
            referencedException.setKey("supplyDetail.deviceCurrentSupply.supplyDetail.referenced");
            referencedException.addParam(supplyDetailDeviceCurrentSupply.getId());
            throw referencedException;
        }
    }

}
