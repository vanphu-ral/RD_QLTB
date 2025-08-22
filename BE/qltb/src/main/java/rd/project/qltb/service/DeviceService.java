package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.DepreciationManagement;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.DeviceGroup;
import rd.project.qltb.domain.DeviceHistory;
import rd.project.qltb.domain.DeviceRelocationHistory;
import rd.project.qltb.domain.DeviceSupplyUsage;
import rd.project.qltb.domain.Line;
import rd.project.qltb.domain.PerformanceManagement;
import rd.project.qltb.domain.PlanDetail;
import rd.project.qltb.model.DeviceDTO;
import rd.project.qltb.repos.BranchRepository;
import rd.project.qltb.repos.DepreciationManagementRepository;
import rd.project.qltb.repos.DeviceGroupRepository;
import rd.project.qltb.repos.DeviceHistoryRepository;
import rd.project.qltb.repos.DeviceRelocationHistoryRepository;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.repos.DeviceSupplyUsageRepository;
import rd.project.qltb.repos.LineRepository;
import rd.project.qltb.repos.PerformanceManagementRepository;
import rd.project.qltb.repos.PlanDetailRepository;
import rd.project.qltb.util.NotFoundException;
import rd.project.qltb.util.ReferencedWarning;


@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceGroupRepository deviceGroupRepository;
    private final LineRepository lineRepository;
    private final BranchRepository branchRepository;
    private final DeviceHistoryRepository deviceHistoryRepository;
    private final DeviceRelocationHistoryRepository deviceRelocationHistoryRepository;
    private final DeviceSupplyUsageRepository deviceSupplyUsageRepository;
    private final PlanDetailRepository planDetailRepository;
    private final PerformanceManagementRepository performanceManagementRepository;
    private final DepreciationManagementRepository depreciationManagementRepository;

    public DeviceService(final DeviceRepository deviceRepository,
            final DeviceGroupRepository deviceGroupRepository, final LineRepository lineRepository,
            final BranchRepository branchRepository,
            final DeviceHistoryRepository deviceHistoryRepository,
            final DeviceRelocationHistoryRepository deviceRelocationHistoryRepository,
            final DeviceSupplyUsageRepository deviceSupplyUsageRepository,
            final PlanDetailRepository planDetailRepository,
            final PerformanceManagementRepository performanceManagementRepository,
            final DepreciationManagementRepository depreciationManagementRepository) {
        this.deviceRepository = deviceRepository;
        this.deviceGroupRepository = deviceGroupRepository;
        this.lineRepository = lineRepository;
        this.branchRepository = branchRepository;
        this.deviceHistoryRepository = deviceHistoryRepository;
        this.deviceRelocationHistoryRepository = deviceRelocationHistoryRepository;
        this.deviceSupplyUsageRepository = deviceSupplyUsageRepository;
        this.planDetailRepository = planDetailRepository;
        this.performanceManagementRepository = performanceManagementRepository;
        this.depreciationManagementRepository = depreciationManagementRepository;
    }

    public List<DeviceDTO> findAll() {
        final List<Device> devices = deviceRepository.findAll(Sort.by("id"));
        return devices.stream()
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .toList();
    }

    public DeviceDTO get(final Integer id) {
        return deviceRepository.findById(id)
                .map(device -> mapToDTO(device, new DeviceDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final DeviceDTO deviceDTO) {
        final Device device = new Device();
        mapToEntity(deviceDTO, device);
        return deviceRepository.save(device).getId();
    }

    public void update(final Integer id, final DeviceDTO deviceDTO) {
        final Device device = deviceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceDTO, device);
        deviceRepository.save(device);
    }

    public void delete(final Integer id) {
        deviceRepository.deleteById(id);
    }

    private DeviceDTO mapToDTO(final Device device, final DeviceDTO deviceDTO) {
        deviceDTO.setId(device.getId());
        deviceDTO.setCode(device.getCode());
        deviceDTO.setName(device.getName());
        deviceDTO.setNumMaterialUse(device.getNumMaterialUse());
        deviceDTO.setSerialNumber(device.getSerialNumber());
        deviceDTO.setSource(device.getSource());
        deviceDTO.setInstallationDate(device.getInstallationDate());
        deviceDTO.setMaintenanceCycle(device.getMaintenanceCycle());
        deviceDTO.setDateManufacture(device.getDateManufacture());
        deviceDTO.setUnit(device.getUnit());
        deviceDTO.setPrice(device.getPrice());
        deviceDTO.setStatus(device.getStatus());
        deviceDTO.setQrcode(device.getQrcode());
        deviceDTO.setImg(device.getImg());
        deviceDTO.setUserManager(device.getUserManager());
        deviceDTO.setCreatedAt(device.getCreatedAt());
        deviceDTO.setUpdatedAt(device.getUpdatedAt());
        deviceDTO.setCreatedBy(device.getCreatedBy());
        deviceDTO.setGroup(device.getGroup() == null ? null : device.getGroup().getId());
        deviceDTO.setLine(device.getLine() == null ? null : device.getLine().getId());
        deviceDTO.setBranch(device.getBranch() == null ? null : device.getBranch().getId());
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
        device.setUnit(deviceDTO.getUnit());
        device.setPrice(deviceDTO.getPrice());
        device.setStatus(deviceDTO.getStatus());
        device.setQrcode(deviceDTO.getQrcode());
        device.setImg(deviceDTO.getImg());
        device.setUserManager(deviceDTO.getUserManager());
        device.setCreatedAt(deviceDTO.getCreatedAt());
        device.setUpdatedAt(deviceDTO.getUpdatedAt());
        device.setCreatedBy(deviceDTO.getCreatedBy());
        final DeviceGroup group = deviceDTO.getGroup() == null ? null : deviceGroupRepository.findById(deviceDTO.getGroup())
                .orElseThrow(() -> new NotFoundException("group not found"));
        device.setGroup(group);
        final Line line = deviceDTO.getLine() == null ? null : lineRepository.findById(deviceDTO.getLine())
                .orElseThrow(() -> new NotFoundException("line not found"));
        device.setLine(line);
        final Branch branch = deviceDTO.getBranch() == null ? null : branchRepository.findById(deviceDTO.getBranch())
                .orElseThrow(() -> new NotFoundException("branch not found"));
        device.setBranch(branch);
        return device;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Device device = deviceRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final DeviceHistory deviceDeviceHistory = deviceHistoryRepository.findFirstByDevice(device);
        if (deviceDeviceHistory != null) {
            referencedWarning.setKey("device.deviceHistory.device.referenced");
            referencedWarning.addParam(deviceDeviceHistory.getId());
            return referencedWarning;
        }
        final DeviceRelocationHistory deviceDeviceRelocationHistory = deviceRelocationHistoryRepository.findFirstByDevice(device);
        if (deviceDeviceRelocationHistory != null) {
            referencedWarning.setKey("device.deviceRelocationHistory.device.referenced");
            referencedWarning.addParam(deviceDeviceRelocationHistory.getId());
            return referencedWarning;
        }
        final DeviceSupplyUsage deviceDeviceSupplyUsage = deviceSupplyUsageRepository.findFirstByDevice(device);
        if (deviceDeviceSupplyUsage != null) {
            referencedWarning.setKey("device.deviceSupplyUsage.device.referenced");
            referencedWarning.addParam(deviceDeviceSupplyUsage.getId());
            return referencedWarning;
        }
        final PlanDetail devicePlanDetail = planDetailRepository.findFirstByDevice(device);
        if (devicePlanDetail != null) {
            referencedWarning.setKey("device.planDetail.device.referenced");
            referencedWarning.addParam(devicePlanDetail.getId());
            return referencedWarning;
        }
        final PerformanceManagement devicePerformanceManagement = performanceManagementRepository.findFirstByDevice(device);
        if (devicePerformanceManagement != null) {
            referencedWarning.setKey("device.performanceManagement.device.referenced");
            referencedWarning.addParam(devicePerformanceManagement.getId());
            return referencedWarning;
        }
        final DepreciationManagement deviceDepreciationManagement = depreciationManagementRepository.findFirstByDevice(device);
        if (deviceDepreciationManagement != null) {
            referencedWarning.setKey("device.depreciationManagement.device.referenced");
            referencedWarning.addParam(deviceDepreciationManagement.getId());
            return referencedWarning;
        }
        return null;
    }

}
