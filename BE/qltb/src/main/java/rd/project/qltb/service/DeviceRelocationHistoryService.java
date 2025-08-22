package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.DeviceRelocationHistory;
import rd.project.qltb.model.DeviceRelocationHistoryDTO;
import rd.project.qltb.repos.DeviceRelocationHistoryRepository;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.util.NotFoundException;


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
        deviceRelocationHistoryRepository.deleteById(id);
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
        deviceRelocationHistoryDTO.setDevice(deviceRelocationHistory.getDevice() == null ? null : deviceRelocationHistory.getDevice().getId());
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
        final Device device = deviceRelocationHistoryDTO.getDevice() == null ? null : deviceRepository.findById(deviceRelocationHistoryDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        deviceRelocationHistory.setDevice(device);
        return deviceRelocationHistory;
    }

}
