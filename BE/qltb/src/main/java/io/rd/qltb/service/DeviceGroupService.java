package io.rd.qltb.service;

import io.rd.qltb.config.GlobalConfig;
import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.model.DeviceGroupDTO;
import io.rd.qltb.repos.DeviceGroupRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.ConstantStatusGlobal.APPROVED;
import static io.rd.qltb.config.ConstantStatusGlobal.DELETED;


@Service
public class DeviceGroupService {

    private final DeviceGroupRepository deviceGroupRepository;
    private final ApplicationEventPublisher publisher;
    private final GlobalConfig globalConfig;

    public DeviceGroupService(final DeviceGroupRepository deviceGroupRepository,
                              final ApplicationEventPublisher publisher, GlobalConfig globalConfig) {
        this.deviceGroupRepository = deviceGroupRepository;
        this.publisher = publisher;
        this.globalConfig = globalConfig;
    }

    public List<DeviceGroupDTO> findAll() {
        final List<DeviceGroup> deviceGroups = deviceGroupRepository.findByStatusNotOrderByIdDesc(DELETED);
        return deviceGroups.stream()
                .map(deviceGroup -> mapToDTO(deviceGroup, new DeviceGroupDTO()))
                .toList();
    }
    public List<DeviceGroupDTO> findAllByApprove() {
        final List<DeviceGroup> deviceGroups = deviceGroupRepository.findByStatusOrderByIdDesc(APPROVED);
        return deviceGroups.stream()
                .map(deviceGroup -> mapToDTO(deviceGroup, new DeviceGroupDTO()))
                .toList();
    }
    public DeviceGroupDTO get(final Long id) {
        return deviceGroupRepository.findById(id)
                .map(deviceGroup -> mapToDTO(deviceGroup, new DeviceGroupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceGroupDTO deviceGroupDTO) {
        final DeviceGroup deviceGroup = new DeviceGroup();
        mapToEntity(deviceGroupDTO, deviceGroup);
        DeviceGroup savedDeviceGroup = deviceGroupRepository.save(deviceGroup);
        savedDeviceGroup.setCode(deviceGroupDTO.getCode()+"-"+globalConfig.createNumberPrefix(savedDeviceGroup.getId(),4));
        return deviceGroupRepository.save(savedDeviceGroup).getId();
    }

    public void update(final Long id, final DeviceGroupDTO deviceGroupDTO) {
        final DeviceGroup deviceGroup = deviceGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceGroupDTO, deviceGroup);
        deviceGroupRepository.save(deviceGroup);
    }

    public void delete(final Long id) {
        final DeviceGroup deviceGroup = deviceGroupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        publisher.publishEvent(new BeforeDeleteDeviceGroup(id));
        deviceGroup.setStatus(DELETED);
        deviceGroupRepository.save(deviceGroup);
    }

    public DeviceGroupDTO mapToDTO(final DeviceGroup deviceGroup,
            final DeviceGroupDTO deviceGroupDTO) {
        deviceGroupDTO.setId(deviceGroup.getId());
        deviceGroupDTO.setCode(deviceGroup.getCode());
        deviceGroupDTO.setName(deviceGroup.getName());
        deviceGroupDTO.setDescription(deviceGroup.getDescription());
        deviceGroupDTO.setCreatedAt(deviceGroup.getCreatedAt());
        deviceGroupDTO.setUpdatedAt(deviceGroup.getUpdatedAt());
        deviceGroupDTO.setCreatedBy(deviceGroup.getCreatedBy());
        deviceGroupDTO.setUpdatedBy(deviceGroup.getUpdatedBy());
        deviceGroupDTO.setStatus(deviceGroup.getStatus());
        if(deviceGroup.getGroupDevices() != null) {
            for(Device device : deviceGroup.getGroupDevices()) {
                device.setGroup(null);
                // Loại bỏ các tham chiếu line
                if (device.getLine() != null){
                device.getLine().setTeam(null);
                device.getLine().setLineDevices(null);
                }

                // Loại bỏ các tham chiếu branch
                if (device.getBranch() != null){
                device.getBranch().setFactory(null);
                device.getBranch().setBranchTeams(null);
                device.getBranch().setBranchDevices(null);
                device.getBranch().setSampleReports(null);
                }

                device.setDeviceDeviceParameterUses(null);
                device.setDeviceDeviceRelocationHistories(null);
                device.setDeviceDeviceSupplyUsages(null);
                device.setDevicePlanDetails(null);
                // Loại bỏ các tham chiếu team
                if (device.getTeam() != null){
                device.getTeam().setBranch(null);
                device.getTeam().setTeamLines(null);
                device.getTeam().setTeamDevices(null);
                }
            }
            deviceGroupDTO.setGroupDevices(
                    deviceGroup.getGroupDevices().stream().toList());
        }else {
            deviceGroupDTO.setGroupDevices(null);
        }
        return deviceGroupDTO;
    }
    public DeviceGroupDTO mapToDTO2(final DeviceGroup deviceGroup, final DeviceGroupDTO deviceGroupDTO) {
        // 1. Map thông tin cơ bản của DeviceGroup
        deviceGroupDTO.setId(deviceGroup.getId());
        deviceGroupDTO.setCode(deviceGroup.getCode());
        deviceGroupDTO.setName(deviceGroup.getName());
        deviceGroupDTO.setDescription(deviceGroup.getDescription());
        deviceGroupDTO.setCreatedAt(deviceGroup.getCreatedAt());
        deviceGroupDTO.setUpdatedAt(deviceGroup.getUpdatedAt());
        deviceGroupDTO.setCreatedBy(deviceGroup.getCreatedBy());
        deviceGroupDTO.setUpdatedBy(deviceGroup.getUpdatedBy());
        deviceGroupDTO.setStatus(deviceGroup.getStatus());

        // 2. Xử lý danh sách GroupDevices
        if (deviceGroup.getGroupDevices() != null) {
            List<Device> cleanedDevices = deviceGroup.getGroupDevices().stream().map(device -> {
                // TẠO MỚI object Device để tránh Hibernate tự động Update vào DB
                Device d = new Device();
                d.setId(device.getId());
                d.setCode(device.getCode());
                d.setName(device.getName());
                d.setStatus(device.getStatus());

                // Ngắt liên kết ngược về Group để tránh vòng lặp JSON (Recursive)
                d.setGroup(null);

                // Loại bỏ các tham chiếu Line (Khởi tạo mới object Line chỉ lấy data cần thiết)
                if (device.getLine() != null) {
                    Line line = new Line();
                    line.setId(device.getLine().getId());
                    line.setName(device.getLine().getName());
                    // Set các trường liên quan của Line về null để ngắt chuỗi liên kết
                    line.setTeam(null);
                    line.setLineDevices(null);
                    d.setLine(line);
                }

                // Loại bỏ các tham chiếu Branch
                if (device.getBranch() != null) {
                    Branch branch = new Branch();
                    branch.setId(device.getBranch().getId());
                    branch.setName(device.getBranch().getName());
                    // Ngắt các liên kết sâu hơn của Branch
                    branch.setFactory(null);
                    branch.setBranchTeams(null);
                    branch.setBranchDevices(null);
                    branch.setSampleReports(null);
                    d.setBranch(branch);
                }

                // Loại bỏ các tham chiếu Team
                if (device.getTeam() != null) {
                    Team team = new Team();
                    team.setId(device.getTeam().getId());
                    team.setName(device.getTeam().getName());
                    // Ngắt các liên kết sâu hơn của Team
                    team.setBranch(null);
                    team.setTeamLines(null);
                    team.setTeamDevices(null);
                    d.setTeam(team);
                }

                // Set các list quan hệ khác về null để tránh N+1 Query và JSON nặng
                d.setDeviceDeviceParameterUses(null);
                d.setDeviceDeviceRelocationHistories(null);
                d.setDeviceDeviceSupplyUsages(null);
                d.setDevicePlanDetails(null);

                return d;
            }).toList();

            deviceGroupDTO.setGroupDevices(cleanedDevices);
        } else {
            deviceGroupDTO.setGroupDevices(null);
        }

        return deviceGroupDTO;
    }
    private DeviceGroup mapToEntity(final DeviceGroupDTO deviceGroupDTO,
            final DeviceGroup deviceGroup) {
        deviceGroup.setCode(deviceGroupDTO.getCode());
        deviceGroup.setName(deviceGroupDTO.getName());
        deviceGroup.setDescription(deviceGroupDTO.getDescription());
        deviceGroup.setCreatedAt(deviceGroupDTO.getCreatedAt());
        deviceGroup.setUpdatedAt(deviceGroupDTO.getUpdatedAt());
        deviceGroup.setCreatedBy(deviceGroupDTO.getCreatedBy());
        deviceGroup.setUpdatedBy(deviceGroupDTO.getUpdatedBy());
        deviceGroup.setStatus(deviceGroupDTO.getStatus());
        return deviceGroup;
    }

}
