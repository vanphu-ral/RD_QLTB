package io.qltb.qltb.service;

import io.qltb.qltb.domain.Device;
import io.qltb.qltb.domain.DeviceHistory;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.model.DeviceHistoryDTO;
import io.qltb.qltb.repos.DeviceHistoryRepository;
import io.qltb.qltb.repos.DeviceRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DeviceHistoryService {

    private final DeviceHistoryRepository deviceHistoryRepository;
    private final DeviceRepository deviceRepository;

    public DeviceHistoryService(final DeviceHistoryRepository deviceHistoryRepository,
            final DeviceRepository deviceRepository) {
        this.deviceHistoryRepository = deviceHistoryRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<DeviceHistoryDTO> findAll() {
        final List<DeviceHistory> deviceHistories = deviceHistoryRepository.findAll(Sort.by("id"));
        return deviceHistories.stream()
                .map(deviceHistory -> mapToDTO(deviceHistory, new DeviceHistoryDTO()))
                .toList();
    }

    public DeviceHistoryDTO get(final Long id) {
        return deviceHistoryRepository.findById(id)
                .map(deviceHistory -> mapToDTO(deviceHistory, new DeviceHistoryDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceHistoryDTO deviceHistoryDTO) {
        final DeviceHistory deviceHistory = new DeviceHistory();
        mapToEntity(deviceHistoryDTO, deviceHistory);
        return deviceHistoryRepository.save(deviceHistory).getId();
    }

    public void update(final Long id, final DeviceHistoryDTO deviceHistoryDTO) {
        final DeviceHistory deviceHistory = deviceHistoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceHistoryDTO, deviceHistory);
        deviceHistoryRepository.save(deviceHistory);
    }

    public void delete(final Long id) {
        final DeviceHistory deviceHistory = deviceHistoryRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        deviceHistoryRepository.delete(deviceHistory);
    }

    private DeviceHistoryDTO mapToDTO(final DeviceHistory deviceHistory,
            final DeviceHistoryDTO deviceHistoryDTO) {
        deviceHistoryDTO.setId(deviceHistory.getId());
        deviceHistoryDTO.setEventType(deviceHistory.getEventType());
        deviceHistoryDTO.setDescription(deviceHistory.getDescription());
        deviceHistoryDTO.setDowntimeStart(deviceHistory.getDowntimeStart());
        deviceHistoryDTO.setDowntimeEnd(deviceHistory.getDowntimeEnd());
        deviceHistoryDTO.setCreatedAt(deviceHistory.getCreatedAt());
        deviceHistoryDTO.setUpdatedAt(deviceHistory.getUpdatedAt());
        deviceHistoryDTO.setCreatedBy(deviceHistory.getCreatedBy());
        deviceHistoryDTO.setUpdateBy(deviceHistory.getUpdateBy());
        deviceHistoryDTO.setDevice(deviceHistory.getDevice() == null ? null : deviceHistory.getDevice().getId());
        return deviceHistoryDTO;
    }

    private DeviceHistory mapToEntity(final DeviceHistoryDTO deviceHistoryDTO,
            final DeviceHistory deviceHistory) {
        deviceHistory.setEventType(deviceHistoryDTO.getEventType());
        deviceHistory.setDescription(deviceHistoryDTO.getDescription());
        deviceHistory.setDowntimeStart(deviceHistoryDTO.getDowntimeStart());
        deviceHistory.setDowntimeEnd(deviceHistoryDTO.getDowntimeEnd());
        deviceHistory.setCreatedAt(deviceHistoryDTO.getCreatedAt());
        deviceHistory.setUpdatedAt(deviceHistoryDTO.getUpdatedAt());
        deviceHistory.setCreatedBy(deviceHistoryDTO.getCreatedBy());
        deviceHistory.setUpdateBy(deviceHistoryDTO.getUpdateBy());
        final Device device = deviceHistoryDTO.getDevice() == null ? null : deviceRepository.findById(deviceHistoryDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        deviceHistory.setDevice(device);
        return deviceHistory;
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final DeviceHistory deviceDeviceHistory = deviceHistoryRepository.findFirstByDeviceId(event.getId());
        if (deviceDeviceHistory != null) {
            referencedException.setKey("device.deviceHistory.device.referenced");
            referencedException.addParam(deviceDeviceHistory.getId());
            throw referencedException;
        }
    }

}
