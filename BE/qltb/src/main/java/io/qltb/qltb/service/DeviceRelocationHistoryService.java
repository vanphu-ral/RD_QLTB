package io.qltb.qltb.service;

import io.qltb.qltb.domain.Device;
import io.qltb.qltb.domain.DeviceRelocationHistory;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.model.DeviceRelocationHistoryDTO;
import io.qltb.qltb.repos.DeviceRelocationHistoryRepository;
import io.qltb.qltb.repos.DeviceRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DeviceRelocationHistoryService {

    private final DeviceRelocationHistoryRepository deviceRelocationHistoryRepository;
    private final DeviceRepository deviceRepository;

    public DeviceRelocationHistoryService(
            final DeviceRelocationHistoryRepository deviceRelocationHistoryRepository,
            final DeviceRepository deviceRepository) {
        this.deviceRelocationHistoryRepository = deviceRelocationHistoryRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<DeviceRelocationHistoryDTO> findAll() {
        final List<DeviceRelocationHistory> deviceRelocationHistories = deviceRelocationHistoryRepository.findAll(Sort.by("id"));
        return deviceRelocationHistories.stream()
                .map(deviceRelocationHistory -> mapToDTO(deviceRelocationHistory, new DeviceRelocationHistoryDTO()))
                .toList();
    }

    public DeviceRelocationHistoryDTO get(final Long id) {
        return deviceRelocationHistoryRepository.findById(id)
                .map(deviceRelocationHistory -> mapToDTO(deviceRelocationHistory, new DeviceRelocationHistoryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceRelocationHistoryDTO deviceRelocationHistoryDTO) {
        final DeviceRelocationHistory deviceRelocationHistory = new DeviceRelocationHistory();
        mapToEntity(deviceRelocationHistoryDTO, deviceRelocationHistory);
        return deviceRelocationHistoryRepository.save(deviceRelocationHistory).getId();
    }

    public void update(final Long id, final DeviceRelocationHistoryDTO deviceRelocationHistoryDTO) {
        final DeviceRelocationHistory deviceRelocationHistory = deviceRelocationHistoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceRelocationHistoryDTO, deviceRelocationHistory);
        deviceRelocationHistoryRepository.save(deviceRelocationHistory);
    }

    public void delete(final Long id) {
        final DeviceRelocationHistory deviceRelocationHistory = deviceRelocationHistoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        deviceRelocationHistoryRepository.delete(deviceRelocationHistory);
    }

    private DeviceRelocationHistoryDTO mapToDTO(
            final DeviceRelocationHistory deviceRelocationHistory,
            final DeviceRelocationHistoryDTO deviceRelocationHistoryDTO) {
        deviceRelocationHistoryDTO.setId(deviceRelocationHistory.getId());
        deviceRelocationHistoryDTO.setOldFactoryId(deviceRelocationHistory.getOldFactoryId());
        deviceRelocationHistoryDTO.setNewFactoryId(deviceRelocationHistory.getNewFactoryId());
        deviceRelocationHistoryDTO.setOldBranchId(deviceRelocationHistory.getOldBranchId());
        deviceRelocationHistoryDTO.setNewBranchId(deviceRelocationHistory.getNewBranchId());
        deviceRelocationHistoryDTO.setOldTeamId(deviceRelocationHistory.getOldTeamId());
        deviceRelocationHistoryDTO.setNewTeamId(deviceRelocationHistory.getNewTeamId());
        deviceRelocationHistoryDTO.setOldLineId(deviceRelocationHistory.getOldLineId());
        deviceRelocationHistoryDTO.setNewLineId(deviceRelocationHistory.getNewLineId());
        deviceRelocationHistoryDTO.setReason(deviceRelocationHistory.getReason());
        deviceRelocationHistoryDTO.setMovedAt(deviceRelocationHistory.getMovedAt());
        deviceRelocationHistoryDTO.setMovedBy(deviceRelocationHistory.getMovedBy());
        deviceRelocationHistoryDTO.setCreatedAt(deviceRelocationHistory.getCreatedAt());
        deviceRelocationHistoryDTO.setUpdatedAt(deviceRelocationHistory.getUpdatedAt());
        deviceRelocationHistoryDTO.setCreatedBy(deviceRelocationHistory.getCreatedBy());
        deviceRelocationHistoryDTO.setUpdatedBy(deviceRelocationHistory.getUpdatedBy());
        deviceRelocationHistoryDTO.setDevice(deviceRelocationHistory.getDevice() == null ? null : deviceRelocationHistory.getDevice());
        return deviceRelocationHistoryDTO;
    }

    private DeviceRelocationHistory mapToEntity(
            final DeviceRelocationHistoryDTO deviceRelocationHistoryDTO,
            final DeviceRelocationHistory deviceRelocationHistory) {
        deviceRelocationHistory.setOldFactoryId(deviceRelocationHistoryDTO.getOldFactoryId());
        deviceRelocationHistory.setNewFactoryId(deviceRelocationHistoryDTO.getNewFactoryId());
        deviceRelocationHistory.setOldBranchId(deviceRelocationHistoryDTO.getOldBranchId());
        deviceRelocationHistory.setNewBranchId(deviceRelocationHistoryDTO.getNewBranchId());
        deviceRelocationHistory.setOldTeamId(deviceRelocationHistoryDTO.getOldTeamId());
        deviceRelocationHistory.setNewTeamId(deviceRelocationHistoryDTO.getNewTeamId());
        deviceRelocationHistory.setOldLineId(deviceRelocationHistoryDTO.getOldLineId());
        deviceRelocationHistory.setNewLineId(deviceRelocationHistoryDTO.getNewLineId());
        deviceRelocationHistory.setReason(deviceRelocationHistoryDTO.getReason());
        deviceRelocationHistory.setMovedAt(deviceRelocationHistoryDTO.getMovedAt());
        deviceRelocationHistory.setMovedBy(deviceRelocationHistoryDTO.getMovedBy());
        deviceRelocationHistory.setCreatedAt(deviceRelocationHistoryDTO.getCreatedAt());
        deviceRelocationHistory.setUpdatedAt(deviceRelocationHistoryDTO.getUpdatedAt());
        deviceRelocationHistory.setCreatedBy(deviceRelocationHistoryDTO.getCreatedBy());
        deviceRelocationHistory.setUpdatedBy(deviceRelocationHistoryDTO.getUpdatedBy());
        final Device device = deviceRelocationHistoryDTO.getDevice() == null ? null : deviceRepository.findById(deviceRelocationHistoryDTO.getDevice().getId())
                .orElseThrow(() -> new NotFoundException("device not found"));
        deviceRelocationHistory.setDevice(device);
        return deviceRelocationHistory;
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final DeviceRelocationHistory deviceDeviceRelocationHistory = deviceRelocationHistoryRepository.findFirstByDeviceId(event.getId());
        if (deviceDeviceRelocationHistory != null) {
            referencedException.setKey("device.deviceRelocationHistory.device.referenced");
            referencedException.addParam(deviceDeviceRelocationHistory.getId());
            throw referencedException;
        }
    }

}
