package io.qltb.qltb.service;

import io.qltb.qltb.domain.DepreciationManagement;
import io.qltb.qltb.domain.Device;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.model.DepreciationManagementDTO;
import io.qltb.qltb.repos.DepreciationManagementRepository;
import io.qltb.qltb.repos.DeviceRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DepreciationManagementService {

    private final DepreciationManagementRepository depreciationManagementRepository;
    private final DeviceRepository deviceRepository;

    public DepreciationManagementService(
            final DepreciationManagementRepository depreciationManagementRepository,
            final DeviceRepository deviceRepository) {
        this.depreciationManagementRepository = depreciationManagementRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<DepreciationManagementDTO> findAll() {
        final List<DepreciationManagement> depreciationManagements = depreciationManagementRepository.findAll(Sort.by("id"));
        return depreciationManagements.stream()
                .map(depreciationManagement -> mapToDTO(depreciationManagement, new DepreciationManagementDTO()))
                .toList();
    }

    public DepreciationManagementDTO get(final Long id) {
        return depreciationManagementRepository.findById(id)
                .map(depreciationManagement -> mapToDTO(depreciationManagement, new DepreciationManagementDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DepreciationManagementDTO depreciationManagementDTO) {
        final DepreciationManagement depreciationManagement = new DepreciationManagement();
        mapToEntity(depreciationManagementDTO, depreciationManagement);
        return depreciationManagementRepository.save(depreciationManagement).getId();
    }

    public void update(final Long id, final DepreciationManagementDTO depreciationManagementDTO) {
        final DepreciationManagement depreciationManagement = depreciationManagementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(depreciationManagementDTO, depreciationManagement);
        depreciationManagementRepository.save(depreciationManagement);
    }

    public void delete(final Long id) {
        final DepreciationManagement depreciationManagement = depreciationManagementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        depreciationManagementRepository.delete(depreciationManagement);
    }

    private DepreciationManagementDTO mapToDTO(final DepreciationManagement depreciationManagement,
                                               final DepreciationManagementDTO depreciationManagementDTO) {
        depreciationManagementDTO.setId(depreciationManagement.getId());
        depreciationManagementDTO.setCode(depreciationManagement.getCode());
        depreciationManagementDTO.setName(depreciationManagement.getName());
        depreciationManagementDTO.setDepr(depreciationManagement.getDepr());
        depreciationManagementDTO.setCreatedAt(depreciationManagement.getCreatedAt());
        depreciationManagementDTO.setUpdatedAt(depreciationManagement.getUpdatedAt());
        depreciationManagementDTO.setCreatedBy(depreciationManagement.getCreatedBy());
        depreciationManagementDTO.setUpdatedBy(depreciationManagement.getUpdatedBy());
        depreciationManagementDTO.setStatus(depreciationManagement.getStatus());

        if (depreciationManagement.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(depreciationManagement.getDevice().getId());
            deviceCopy.setCode(depreciationManagement.getDevice().getCode());
            deviceCopy.setName(depreciationManagement.getDevice().getName());
            deviceCopy.setStatus(depreciationManagement.getDevice().getStatus());
            deviceCopy.setCreatedAt(depreciationManagement.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(depreciationManagement.getDevice().getUpdatedAt());
            deviceCopy.setCreatedBy(depreciationManagement.getDevice().getCreatedBy());
            deviceCopy.setUpdatedBy(depreciationManagement.getDevice().getUpdatedBy());

            // Xóa các quan hệ con để tránh vòng lặp hoặc dữ liệu thừa
            deviceCopy.setGroup(null);
            deviceCopy.setLine(null);
            deviceCopy.setDevicePlanDetails(null);
            deviceCopy.setDeviceDeviceHistories(null);
            deviceCopy.setDeviceDeviceSupplyUsages(null);
            deviceCopy.setDeviceDeviceRelocationHistories(null);
            deviceCopy.setDevicePrameters(null);
            deviceCopy.setDevicePerformanceManagements(null);
            deviceCopy.setDeviceDepreciationManagements(null);

            depreciationManagementDTO.setDevice(deviceCopy);
        } else {
            depreciationManagementDTO.setDevice(null);
        }

        return depreciationManagementDTO;
    }


    private DepreciationManagement mapToEntity(
            final DepreciationManagementDTO depreciationManagementDTO,
            final DepreciationManagement depreciationManagement) {
        depreciationManagement.setCode(depreciationManagementDTO.getCode());
        depreciationManagement.setName(depreciationManagementDTO.getName());
        depreciationManagement.setDepr(depreciationManagementDTO.getDepr());
        depreciationManagement.setCreatedAt(depreciationManagementDTO.getCreatedAt());
        depreciationManagement.setUpdatedAt(depreciationManagementDTO.getUpdatedAt());
        depreciationManagement.setCreatedBy(depreciationManagementDTO.getCreatedBy());
        depreciationManagement.setUpdatedBy(depreciationManagementDTO.getUpdatedBy());
        depreciationManagement.setStatus(depreciationManagementDTO.getStatus());
        final Device device = depreciationManagementDTO.getDevice() == null ? null : deviceRepository.findById(depreciationManagementDTO.getDevice().getId())
                .orElseThrow(() -> new NotFoundException("device not found"));
        depreciationManagement.setDevice(device);
        return depreciationManagement;
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final DepreciationManagement deviceDepreciationManagement = depreciationManagementRepository.findFirstByDeviceId(event.getId());
        if (deviceDepreciationManagement != null) {
            referencedException.setKey("device.depreciationManagement.device.referenced");
            referencedException.addParam(deviceDepreciationManagement.getId());
            throw referencedException;
        }
    }

}
