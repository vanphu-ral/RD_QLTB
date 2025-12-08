package io.rd.qltb.service;

import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeleteDevice;
import io.rd.qltb.model.DeviceRelocationHistoryDTO;
import io.rd.qltb.model.DeviceRelocationHistoryViewDTO;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DeviceRelocationHistoryService {

    private final DeviceRelocationHistoryRepository deviceRelocationHistoryRepository;
    private final DeviceRepository deviceRepository;
    private final FactoryRepository factoryRepository;
    private final BranchRepository branchRepository;
    private final LineRepository lineRepository;
    private final TeamRepository teamRepository;

    public DeviceRelocationHistoryService(
            final DeviceRelocationHistoryRepository deviceRelocationHistoryRepository,
            final DeviceRepository deviceRepository,
            final FactoryRepository factoryRepository,
            final BranchRepository branchRepository,
            final LineRepository lineRepository,
            final TeamRepository teamRepository) {
        this.deviceRelocationHistoryRepository = deviceRelocationHistoryRepository;
        this.deviceRepository = deviceRepository;
        this.factoryRepository = factoryRepository;
        this.branchRepository = branchRepository;
        this.lineRepository = lineRepository;
        this.teamRepository = teamRepository;
    }

    public List<DeviceRelocationHistoryDTO> findAll() {
        final List<DeviceRelocationHistory> deviceRelocationHistories = deviceRelocationHistoryRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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

    public List<DeviceRelocationHistoryViewDTO> findAllByDeviceId(Long deviceId) {
        final List<DeviceRelocationHistory> histories =
                deviceRelocationHistoryRepository.findAllByDeviceIdOrderByMovedAtDesc(deviceId);

        return histories.stream()
                .map(history -> mapToDTOWithFullRelations(history, new DeviceRelocationHistoryViewDTO()))
                .toList();
    }

    private DeviceRelocationHistoryViewDTO mapToDTOWithFullRelations(
            final DeviceRelocationHistory deviceRelocationHistory,
            final DeviceRelocationHistoryViewDTO dto) {

        // Dùng lại mapToDTO gốc
        mapToDTO(deviceRelocationHistory, dto);

        // Map thêm dữ liệu mô tả cho old/new nếu có
        // (Giả sử có EntityFactory, Branch, Team, Line tương ứng)
        if (deviceRelocationHistory.getOldFactoryId() != null) {
            dto.setOldFactoryName(getNameById("Factory", deviceRelocationHistory.getOldFactoryId()));
        }
        if (deviceRelocationHistory.getNewFactoryId() != null) {
            dto.setNewFactoryName(getNameById("Factory", deviceRelocationHistory.getNewFactoryId()));
        }

        if (deviceRelocationHistory.getOldBranchId() != null) {
            dto.setOldBranchName(getNameById("Branch", deviceRelocationHistory.getOldBranchId()));
        }
        if (deviceRelocationHistory.getNewBranchId() != null) {
            dto.setNewBranchName(getNameById("Branch", deviceRelocationHistory.getNewBranchId()));
        }

        if (deviceRelocationHistory.getOldTeamId() != null) {
            dto.setOldTeamName(getNameById("Team", deviceRelocationHistory.getOldTeamId()));
        }
        if (deviceRelocationHistory.getNewTeamId() != null) {
            dto.setNewTeamName(getNameById("Team", deviceRelocationHistory.getNewTeamId()));
        }

        if (deviceRelocationHistory.getOldLineId() != null) {
            dto.setOldLineName(getNameById("Line", deviceRelocationHistory.getOldLineId()));
        }
        if (deviceRelocationHistory.getNewLineId() != null) {
            dto.setNewLineName(getNameById("Line", deviceRelocationHistory.getNewLineId()));
        }

        return dto;
    }

    private String getNameById(String entityType, Long id) {
        return switch (entityType) {
            case "Factory" ->
                    factoryRepository.findById(id)
                            .map(Factory::getName)
                            .orElse(null);
            case "Branch" ->
                    branchRepository.findById(id)
                            .map(Branch::getName)
                            .orElse(null);
            case "Team" ->
                    teamRepository.findById(id)
                            .map(Team::getName)
                            .orElse(null);
            case "Line" ->
                    lineRepository.findById(id)
                            .map(Line::getName)
                            .orElse(null);
            default -> null;
        };
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

        // Sao chép Device có kiểm soát
        if (deviceRelocationHistory.getDevice() != null) {
            Device deviceCopy = new Device();
            deviceCopy.setId(deviceRelocationHistory.getDevice().getId());
            deviceCopy.setCode(deviceRelocationHistory.getDevice().getCode());
            deviceCopy.setName(deviceRelocationHistory.getDevice().getName());
            deviceCopy.setNumMaterialUse(deviceRelocationHistory.getDevice().getNumMaterialUse());
            deviceCopy.setSerialNumber(deviceRelocationHistory.getDevice().getSerialNumber());
            deviceCopy.setSource(deviceRelocationHistory.getDevice().getSource());
            deviceCopy.setInstallationDate(deviceRelocationHistory.getDevice().getInstallationDate());
            deviceCopy.setMaintenanceCycle(deviceRelocationHistory.getDevice().getMaintenanceCycle());
            deviceCopy.setDateManufacture(deviceRelocationHistory.getDevice().getDateManufacture());
            deviceCopy.setUnit(deviceRelocationHistory.getDevice().getUnit());
            deviceCopy.setPrice(deviceRelocationHistory.getDevice().getPrice());
            deviceCopy.setStatus(deviceRelocationHistory.getDevice().getStatus());
            deviceCopy.setQrCode(deviceRelocationHistory.getDevice().getQrCode());
            deviceCopy.setQrCodeImg(deviceRelocationHistory.getDevice().getQrCodeImg());
            deviceCopy.setImg(deviceRelocationHistory.getDevice().getImg());
            deviceCopy.setUserManager(deviceRelocationHistory.getDevice().getUserManager());
            deviceCopy.setCreatedAt(deviceRelocationHistory.getDevice().getCreatedAt());
            deviceCopy.setUpdatedAt(deviceRelocationHistory.getDevice().getUpdatedAt());
            deviceCopy.setCreatedBy(deviceRelocationHistory.getDevice().getCreatedBy());
            deviceCopy.setUpdatedBy(deviceRelocationHistory.getDevice().getUpdatedBy());
            deviceCopy.setSupplier(deviceRelocationHistory.getDevice().getSupplier());

            // Xóa các quan hệ con để tránh vòng lặp
            deviceCopy.setGroup(null);
            deviceCopy.setLine(null);
            deviceCopy.setBranch(null);
            deviceCopy.setTeam(null);
            deviceCopy.setDeviceDeviceParameterUses(null);
            deviceCopy.setDeviceDeviceRelocationHistories(null);
            deviceCopy.setDeviceDeviceSupplyUsages(null);
            deviceCopy.setDevicePlanDetails(null);

            deviceRelocationHistoryDTO.setDevice(deviceCopy);
        } else {
            deviceRelocationHistoryDTO.setDevice(null);
        }

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
