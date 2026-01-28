package io.rd.qltb.service;

import io.rd.qltb.config.GlobalConfig;
import io.rd.qltb.domain.*;
import io.rd.qltb.events.BeforeDeleteBranch;
import io.rd.qltb.events.BeforeDeleteDeviceGroup;
import io.rd.qltb.events.BeforeDeleteLine;
import io.rd.qltb.events.BeforeDeleteTeam;
import io.rd.qltb.model.DeviceDTO;
import io.rd.qltb.model.DeviceGroupDTO;
import io.rd.qltb.model.response.DeviceMaintenanceDTO;
import io.rd.qltb.repos.*;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.criteria.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import static io.rd.qltb.config.ConstantStatusGlobal.*;


@Service
public class DeviceService {
    @PersistenceContext
    private EntityManager entityManager;
    private final DeviceRepository deviceRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final DeviceGroupService deviceGroupService;
    private final LineRepository lineRepository;
    private final BranchRepository branchRepository;
    private final TeamRepository teamRepository;
    private final ApplicationEventPublisher publisher;
    private final GlobalConfig globalConfig ;
    private final PlanResultDetailRepository planResultDetailRepository;
    private final PlanResultRepository planResultRepository;

    public DeviceService(EntityManager entityManager, final DeviceRepository deviceRepository,
                         final DeviceGroupRepository deviceGroupRepository, DeviceGroupService deviceGroupService, final LineRepository lineRepository,
                         final BranchRepository branchRepository, final TeamRepository teamRepository,
                         final ApplicationEventPublisher publisher, GlobalConfig globalConfig, PlanResultDetailRepository planResultDetailRepository, PlanResultRepository planResultRepository) {
        this.entityManager = entityManager;
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.deviceGroupService = deviceGroupService;
        this.lineRepository = lineRepository;
        this.branchRepository = branchRepository;
        this.teamRepository = teamRepository;
        this.publisher = publisher;
        this.globalConfig = globalConfig;
        this.planResultDetailRepository = planResultDetailRepository;
        this.planResultRepository = planResultRepository;
    }
    @Transactional
    public Page<DeviceDTO> findDevicesPaged(Map<String, Object> filters, int page) {
        int pageSize = 10;
        var cb = entityManager.getCriteriaBuilder();

        // Query chính
        var cq = cb.createQuery(Device.class);
        var root = cq.from(Device.class);

        // Query count
        var countQuery = cb.createQuery(Long.class);
        var countRoot = countQuery.from(Device.class);

        // Xây dựng Predicates
        Predicate[] dataPredicates = buildPredicates(filters, cb, root);
        Predicate[] countPredicates = buildPredicates(filters, cb, countRoot);

        // Execute Data Query
        cq.where(dataPredicates);
        var query = entityManager.createQuery(cq);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);

        List<DeviceDTO> dtos = query.getResultList().stream()
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .toList();

        // Execute Count Query
        countQuery.select(cb.count(countRoot)).where(countPredicates);
        Long totalRecords = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(dtos, PageRequest.of(page, pageSize), totalRecords);
    }
    public Page<DeviceMaintenanceDTO> getUpcomingMaintenance(
            String deviceName,
            String groupName,
            String lineName,
            String teamName,
            String branchName,
            Pageable pageable
    ) {
        // Chuyển chuỗi rỗng thành null để query WHERE (:param IS NULL) hoạt động đúng
        String dName = (deviceName != null && !deviceName.trim().isEmpty()) ? deviceName : null;
        String gName = (groupName != null && !groupName.trim().isEmpty()) ? groupName : null;
        String lName = (lineName != null && !lineName.trim().isEmpty()) ? lineName : null;
        String tName = (teamName != null && !teamName.trim().isEmpty()) ? teamName : null;
        String bName = (branchName != null && !branchName.trim().isEmpty()) ? branchName : null;

        Page<Object[]> results = deviceRepository.findDevicesNotYetDue(dName, gName, lName,bName, tName, pageable);

        return results.map(row -> {
            // Xử lý ngày tháng
            LocalDate dateTest = convertToLocalDate(row[3]);
            LocalDate nextTest = convertToLocalDate(row[5]);

            // Xử lý số ngày chênh lệch (Index 8: days_until_next)
            Long daysDiff = row[8] != null ? ((Number) row[8]).longValue() : null;

            // Logic trạng thái
            String status = (nextTest == null) ? "NO_DATA" : "UPCOMING";

            // Mapping dữ liệu vào DTO (Index từ 0 đến 12)
            return new DeviceMaintenanceDTO(
                    row[0] != null ? ((Number) row[0]).longValue() : null,    // deviceId
                    asString(row[1]),                                         // deviceName
                    row[2] != null ? ((Number) row[2]).intValue() : null,    // maintenanceTime
                    dateTest,                                                 // dateTest
                    asString(row[4]),                                         // estimatedTime
                    nextTest,                                                 // nextTest
                    asString(row[6]),                                         // planName
                    row[7] != null ? ((Number) row[7]).longValue() : null,    // planResultId
                    daysDiff,                                                 // daysDiff
                    status,                                                   // status
                    asString(row[9]),                                         // deviceGroupName
                    asString(row[10]),                                        // branchName
                    asString(row[11]),                                        // teamName
                    asString(row[12])                                         // lineName
            );
        });
    }

    /**
     * Helper method để chuyển Object sang String an toàn
     */
    private String asString(Object obj) {
        return obj != null ? obj.toString() : null;
    }

    // Hàm hỗ trợ để tránh lặp lại logic và xử lý ép kiểu an toàn
    private LocalDate convertToLocalDate(Object obj) {
        if (obj == null) return null;

        // Nếu là Timestamp (thường gặp khi dùng SQL Server/MySQL DATETIME)
        if (obj instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) obj).toLocalDateTime().toLocalDate();
        }
        // Nếu là Date (thường gặp khi dùng SQL DATE)
        if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).toLocalDate();
        }
        // Trường hợp dự phòng nếu là java.util.Date
        if (obj instanceof java.util.Date) {
            return ((java.util.Date) obj).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }
    public Page<DeviceMaintenanceDTO> getMaintenanceReport(
            String deviceName,
            String branchName,
            String groupName,
            String teamName,
            String lineName,
            String filterType,
            Pageable pageable) {

        // 1. Chuẩn hóa tham số (Chuyển chuỗi rỗng thành null để SQL xử lý :param IS NULL)
        String d = cleanParam(deviceName);
        String b = cleanParam(branchName);
        String g = cleanParam(groupName);
        String t = cleanParam(teamName);
        String l = cleanParam(lineName);
        String type = (filterType == null) ? "ALL" : filterType.toUpperCase();

        // 2. Gọi Repository với đầy đủ 6 tham số lọc
        Page<Object[]> rawResults = deviceRepository.findMaintenanceData(d, b, g, t, l, type, pageable);

        // 3. Mapping dữ liệu từ Object[] sang DTO
        return rawResults.map(row -> {
            // Chuyển đổi ngày tháng an toàn (hỗ trợ cả java.sql.Date và java.sql.Timestamp)
            LocalDate dateTest = convertToLocalDate(row[3]);
            LocalDate nextTest = convertToLocalDate(row[5]);

            // Lấy số ngày chênh lệch (Index 8: days_until_next)
            Long diff = row[8] != null ? ((Number) row[8]).longValue() : null;

            // Logic trạng thái chính xác hơn
            String status = "NO_DATA";
            if (nextTest != null) {
                // Lưu ý: DATEDIFF(next_test, CURDATE()) > 0 là còn hạn, < 0 là quá hạn
                status = (diff != null && diff < 0) ? "OVERDUE" : "UPCOMING";
            }

            return new DeviceMaintenanceDTO(
                    ((Number) row[0]).longValue(),              // deviceId
                    asString(row[1]),                            // deviceName
                    row[2] != null ? ((Number) row[2]).intValue() : null, // maintenanceTime
                    dateTest,                                    // dateTest
                    asString(row[4]),                            // estimatedTime
                    nextTest,                                    // nextTest
                    asString(row[6]),                            // planName
                    row[7] != null ? ((Number) row[7]).longValue() : null, // planResultId
                    diff,                                        // daysDiff
                    status,                                      // status
                    asString(row[9]),                            // deviceGroupName
                    asString(row[10]),                           // branchName
                    asString(row[11]),                           // teamName
                    asString(row[12])                            // lineName
            );
        });
    }

// --- Các hàm hỗ trợ để code sạch hơn ---

    private String cleanParam(String s) {
        return (s != null && !s.trim().isEmpty()) ? s : null;
    }
    private Predicate[] buildPredicates(Map<String, Object> filters, CriteriaBuilder cb, Root<Device> root) {
        List<Predicate> predicates = new ArrayList<>();

        filters.forEach((key, value) -> {
            if (value != null && !value.toString().isEmpty()) {
                Path<?> path;

                // XỬ LÝ JOIN TỰ ĐỘNG
                if (List.of("group", "line", "branch", "team").contains(key)) {
                    // Nếu key là "group", ta mặc định hiểu là muốn lọc theo "group.name"
                    path = root.join(key, JoinType.LEFT).get("name");
                } else if (key.contains(".")) {
                    // Nếu key truyền vào dạng "group.code", "branch.name"
                    String[] parts = key.split("\\.");
                    Join<Object, Object> join = root.join(parts[0], JoinType.LEFT);
                    path = join.get(parts[1]);
                } else {
                    // Trường thông thường trong Device
                    path = root.get(key);
                }

                // PHÂN LOẠI KIỂU DỮ LIỆU ĐỂ TẠO PREDICATE
                if (path.getJavaType().equals(LocalDateTime.class)) {
                    String v = value.toString();
                    LocalDateTime startOfDay = (v.length() == 10)
                            ? LocalDate.parse(v).atStartOfDay()
                            : LocalDateTime.parse(v);
                    LocalDateTime endOfDay = startOfDay.toLocalDate().atTime(LocalTime.MAX);
                    predicates.add(cb.between((Expression<LocalDateTime>) path, startOfDay, endOfDay));
                } else {
                    // Dùng lower-case để tìm kiếm không phân biệt hoa thường
                    predicates.add(cb.like(cb.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%"));
                }
            }
        });

        predicates.add(cb.notEqual(root.get("status"), 10)); // Giả định 10 là DELETED
        return predicates.toArray(new Predicate[0]);
    }

    public List<DeviceDTO> findAll() {
        final List<Device> devices = deviceRepository.findByStatusOrderByIdDesc(DRAFF);
        return devices.stream()
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .toList();
    }
    public List<DeviceDTO> getDevicesByGroupId(Long groupId) {
        final List<Device> devices = deviceRepository.findByGroupIdAndStatusOrderByIdDesc(groupId,DRAFF);
        return devices.stream()
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .toList();
    }
    public List<DeviceGroupDTO> getListDeviceGroupsByBranch(String branchCode) {
        List<Long> deviceGroupIds = deviceRepository.findDistinctBranchIdsByBranchCode(branchCode);
        List<DeviceGroupDTO> deviceGroupDTOS = new ArrayList<>();
        for (Long id : deviceGroupIds) {
          DeviceGroupDTO dto = deviceGroupService.get(id);
          dto.setGroupDevices(null); // Xóa danh sách thiết bị trong nhóm để tránh tải dư thừa
            deviceGroupDTOS.add(dto);
        }
        return deviceGroupDTOS;
    }
    public List<DeviceDTO> getDevicesByGroupIdAndPlanID(Long groupId,Long planId) {
        final List<Device> devices = deviceRepository.findByGroupIdAndStatusOrderByIdDesc(groupId,DRAFF);
        List<DeviceDTO> deviceDTOS =  devices.stream()
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .toList();
        for (DeviceDTO dto : deviceDTOS) {
            Integer count = planResultDetailRepository.countByDeviceIdAndPlanId(dto.getId(),planId);
            if (count != null && count > 0) {
                dto.setIsHadDataPlanReport(1);
            } else {
                dto.setIsHadDataPlanReport(0);
            }
        }

        return deviceDTOS;
    }
    public DeviceDTO get(final Long id) {
        return deviceRepository.findById(id)
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final DeviceDTO deviceDTO) {
        final Device device = new Device();
        mapToEntity(deviceDTO, device);
        Device savedDevice = deviceRepository.save(device);
       savedDevice.setCode(deviceDTO.getCode()+"-"+globalConfig.createNumberPrefix(savedDevice.getId(),6)); // Tạo mã thiết bị theo định dạng
        return deviceRepository.save(savedDevice).getId();
    }
    public List<Long> creates(final List<DeviceDTO> deviceDTO) {
        List<Long> createdIds = new ArrayList<>();
        for (DeviceDTO dto : deviceDTO) {
            Device entity;
            if (dto.getId() != null) {
                entity = deviceRepository.findById(dto.getId()).orElse(new Device());
            } else {
                entity = new Device();
            }
            mapToEntity(dto, entity);
            Device saved = deviceRepository.save(entity);
            saved.setCode(dto.getCode()+"-"+globalConfig.createNumberPrefix(saved.getId(),6)); // Tạo mã thiết bị theo định dạng
            deviceRepository.save(saved);
            createdIds.add(saved.getId());
        }
        return createdIds;
    }
    public void update(final Long id, final DeviceDTO deviceDTO) {
        final Device device = deviceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceDTO, device);
        deviceRepository.save(device);
    }

    public void delete(final Long id) {
        final Device device = deviceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
//        publisher.publishEvent(new BeforeDeleteDevice(id));
        device.setStatus(DELETED);
        deviceRepository.save(device);
    }

    public DeviceDTO getDeviceByQrCode(String qrCode) {
        return deviceRepository.findByQrCode(qrCode)
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public void updateStatus(Long id, Integer status) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Plan not found"));

        device.setStatus(status);
        deviceRepository.save(device);
    }

    public DeviceDTO mapToDTO(final Device device, final DeviceDTO deviceDTO) {
        deviceDTO.setId(device.getId());
        deviceDTO.setCode(device.getCode());
        deviceDTO.setName(device.getName());
        deviceDTO.setNumMaterialUse(device.getNumMaterialUse());
        deviceDTO.setSerialNumber(device.getSerialNumber());
        deviceDTO.setSource(device.getSource());
        deviceDTO.setInstallationDate(device.getInstallationDate());
        deviceDTO.setMaintenanceCycle(device.getMaintenanceCycle());
        deviceDTO.setDateManufacture(device.getDateManufacture());
        deviceDTO.setMaintenanceTime(device.getMaintenanceTime());
        deviceDTO.setDepreciationPeriod(device.getDepreciationPeriod());
        deviceDTO.setDepreciationPercentage(device.getDepreciationPercentage());
        deviceDTO.setUnit(device.getUnit());
        deviceDTO.setPrice(device.getPrice());
        deviceDTO.setStatus(device.getStatus());
        deviceDTO.setQrCode(device.getQrCode());
        deviceDTO.setQrCodeImg(device.getQrCodeImg());
        deviceDTO.setIsMappingScada(device.getIsMappingScada());
        deviceDTO.setIsImportant(device.getIsImportant());
        deviceDTO.setTimeRecieve(device.getTimeRecieve());
        deviceDTO.setImg(device.getImg());
        deviceDTO.setUserManager(device.getUserManager());
        deviceDTO.setDescription(device.getDescription());
        deviceDTO.setCreatedAt(device.getCreatedAt());
        deviceDTO.setUpdatedAt(device.getUpdatedAt());
        deviceDTO.setCreatedBy(device.getCreatedBy());
        deviceDTO.setUpdatedBy(device.getUpdatedBy());
        deviceDTO.setSupplier(device.getSupplier());

        // Sao chép DeviceGroup có kiểm soát
        if (device.getGroup() != null) {
            DeviceGroup groupCopy = new DeviceGroup();
            groupCopy.setId(device.getGroup().getId());
            groupCopy.setCode(device.getGroup().getCode());
            groupCopy.setName(device.getGroup().getName());
            groupCopy.setDescription(device.getGroup().getDescription());
            groupCopy.setCreatedAt(device.getGroup().getCreatedAt());
            groupCopy.setUpdatedAt(device.getGroup().getUpdatedAt());
            groupCopy.setCreatedBy(device.getGroup().getCreatedBy());
            groupCopy.setUpdatedBy(device.getGroup().getUpdatedBy());
            groupCopy.setStatus(device.getGroup().getStatus());

            // Xóa các quan hệ con
            groupCopy.setDeviceGroupSampleReports(null);
            groupCopy.setDeviceGroupKeyMappingDeviceSampleReports(null);
            groupCopy.setGroupDevices(null);
            groupCopy.setDeviceGroupPlanDetails(null);

            deviceDTO.setGroup(groupCopy);
        } else {
            deviceDTO.setGroup(null);
        }

        // Sao chép Line có kiểm soát
        if (device.getLine() != null) {
            Line lineCopy = new Line();
            lineCopy.setId(device.getLine().getId());
            lineCopy.setCode(device.getLine().getCode());
            lineCopy.setName(device.getLine().getName());
            lineCopy.setDescription(device.getLine().getDescription());
            lineCopy.setManager(device.getLine().getManager());
            lineCopy.setCreatedAt(device.getLine().getCreatedAt());
            lineCopy.setUpdatedAt(device.getLine().getUpdatedAt());
            lineCopy.setCreatedBy(device.getLine().getCreatedBy());
            lineCopy.setUpdatedBy(device.getLine().getUpdatedBy());
            lineCopy.setStatus(device.getLine().getStatus());

            // Xóa các quan hệ con
            lineCopy.setTeam(null);
            lineCopy.setLineDevices(null);

            deviceDTO.setLine(lineCopy);
        } else {
            deviceDTO.setLine(null);
        }

        // Sao chép Branch có kiểm soát
        if (device.getBranch() != null) {
            Branch branchCopy = new Branch();
            branchCopy.setId(device.getBranch().getId());
            branchCopy.setCode(device.getBranch().getCode());
            branchCopy.setName(device.getBranch().getName());
            branchCopy.setDescription(device.getBranch().getDescription());
            branchCopy.setManager(device.getBranch().getManager());
            branchCopy.setCreatedAt(device.getBranch().getCreatedAt());
            branchCopy.setUpdatedAt(device.getBranch().getUpdatedAt());
            branchCopy.setCreatedBy(device.getBranch().getCreatedBy());
            branchCopy.setUpdatedBy(device.getBranch().getUpdatedBy());
            branchCopy.setStatus(device.getBranch().getStatus());

            // Xóa các quan hệ con
            branchCopy.setFactory(null);
            branchCopy.setBranchTeams(null);
            branchCopy.setBranchDevices(null);

            deviceDTO.setBranch(branchCopy);
        } else {
            deviceDTO.setBranch(null);
        }

        // Sao chép Team có kiểm soát
        if (device.getTeam() != null) {
            Team teamCopy = new Team();
            teamCopy.setId(device.getTeam().getId());
            teamCopy.setCode(device.getTeam().getCode());
            teamCopy.setName(device.getTeam().getName());
            teamCopy.setDescription(device.getTeam().getDescription());
            teamCopy.setManager(device.getTeam().getManager());
            teamCopy.setCreatedAt(device.getTeam().getCreatedAt());
            teamCopy.setUpdatedAt(device.getTeam().getUpdatedAt());
            teamCopy.setCreatedBy(device.getTeam().getCreatedBy());
            teamCopy.setUpdatedBy(device.getTeam().getUpdatedBy());
            teamCopy.setStatus(device.getTeam().getStatus());

            // Xóa các quan hệ con
            teamCopy.setBranch(null);
            teamCopy.setTeamLines(null);
            teamCopy.setTeamDevices(null);
            deviceDTO.setTeam(teamCopy);
        } else {
            deviceDTO.setTeam(null);
        }

        return deviceDTO;
    }


    private Device mapToEntity(final DeviceDTO deviceDTO, final Device device) {
        device.setCode(deviceDTO.getCode());
        device.setName(deviceDTO.getName());
        device.setNumMaterialUse(deviceDTO.getNumMaterialUse());
        device.setSerialNumber(deviceDTO.getSerialNumber());
        device.setSource(deviceDTO.getSource());
        device.setInstallationDate(deviceDTO.getInstallationDate());
        device.setMaintenanceCycle(deviceDTO.getMaintenanceCycle());
        device.setDateManufacture(deviceDTO.getDateManufacture());
        device.setMaintenanceTime(deviceDTO.getMaintenanceTime());
        device.setDepreciationPeriod(deviceDTO.getDepreciationPeriod());
        device.setDepreciationPercentage(deviceDTO.getDepreciationPercentage());
        device.setUnit(deviceDTO.getUnit());
        device.setPrice(deviceDTO.getPrice());
        device.setStatus(deviceDTO.getStatus());
        device.setQrCode(deviceDTO.getQrCode());
        device.setQrCodeImg(deviceDTO.getQrCodeImg());
        device.setIsMappingScada(deviceDTO.getIsMappingScada());
        device.setIsImportant(deviceDTO.getIsImportant());
        device.setTimeRecieve(deviceDTO.getTimeRecieve());
        device.setImg(deviceDTO.getImg());
        device.setUserManager(deviceDTO.getUserManager());
        device.setDescription(deviceDTO.getDescription());
        device.setCreatedAt(deviceDTO.getCreatedAt());
        device.setUpdatedAt(deviceDTO.getUpdatedAt());
        device.setCreatedBy(deviceDTO.getCreatedBy());
        device.setUpdatedBy(deviceDTO.getUpdatedBy());
        device.setSupplier(deviceDTO.getSupplier());
        final DeviceGroup group = deviceDTO.getGroup() == null ? null : deviceGroupRepository.findById(deviceDTO.getGroup().getId())
                .orElseThrow(() -> new NotFoundException("group not found"));
        device.setGroup(group);
        final Line line = deviceDTO.getLine() == null ? null : lineRepository.findById(deviceDTO.getLine().getId())
                .orElseThrow(() -> new NotFoundException("line not found"));
        device.setLine(line);
        final Branch branch = deviceDTO.getBranch() == null ? null : branchRepository.findById(deviceDTO.getBranch().getId())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        device.setBranch(branch);
        final Team team = deviceDTO.getTeam() == null ? null : teamRepository.findById(deviceDTO.getTeam().getId())
                .orElseThrow(() -> new NotFoundException("team not found"));
        device.setTeam(team);
        return device;
    }

    @EventListener(BeforeDeleteDeviceGroup.class)
    public void on(final BeforeDeleteDeviceGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final Device groupDevice = deviceRepository.findFirstByGroupId(event.getId());
        if (groupDevice != null) {
            referencedException.setKey("deviceGroup.device.group.referenced");
            referencedException.addParam(groupDevice.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteLine.class)
    public void on(final BeforeDeleteLine event) {
        final ReferencedException referencedException = new ReferencedException();
        final Device lineDevice = deviceRepository.findFirstByLineId(event.getId());
        if (lineDevice != null) {
            referencedException.setKey("line.device.line.referenced");
            referencedException.addParam(lineDevice.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteBranch.class)
    public void on(final BeforeDeleteBranch event) {
        final ReferencedException referencedException = new ReferencedException();
        final Device branchDevice = deviceRepository.findFirstByBranchId(event.getId());
        if (branchDevice != null) {
            referencedException.setKey("branch.device.branch.referenced");
            referencedException.addParam(branchDevice.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteTeam.class)
    public void on(final BeforeDeleteTeam event) {
        final ReferencedException referencedException = new ReferencedException();
        final Device teamDevice = deviceRepository.findFirstByTeamId(event.getId());
        if (teamDevice != null) {
            referencedException.setKey("team.device.team.referenced");
            referencedException.addParam(teamDevice.getId());
            throw referencedException;
        }
    }

}
