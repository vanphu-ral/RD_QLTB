package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.DeviceSupplyUsage;
import rd.project.qltb.domain.Supply;
import rd.project.qltb.model.DeviceSupplyUsageDTO;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.repos.DeviceSupplyUsageRepository;
import rd.project.qltb.repos.SupplyRepository;
import rd.project.qltb.util.NotFoundException;


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

    public void update(final Long id, final DeviceSupplyUsageDTO deviceSupplyUsageDTO) {
        final DeviceSupplyUsage deviceSupplyUsage = deviceSupplyUsageRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(deviceSupplyUsageDTO, deviceSupplyUsage);
        deviceSupplyUsageRepository.save(deviceSupplyUsage);
    }

    public void delete(final Long id) {
        deviceSupplyUsageRepository.deleteById(id);
    }

    private DeviceSupplyUsageDTO mapToDTO(final DeviceSupplyUsage deviceSupplyUsage,
            final DeviceSupplyUsageDTO deviceSupplyUsageDTO) {
        deviceSupplyUsageDTO.setId(deviceSupplyUsage.getId());
        deviceSupplyUsageDTO.setQuantityUsed(deviceSupplyUsage.getQuantityUsed());
        deviceSupplyUsageDTO.setUsageDate(deviceSupplyUsage.getUsageDate());
        deviceSupplyUsageDTO.setNote(deviceSupplyUsage.getNote());
        deviceSupplyUsageDTO.setCreatedAt(deviceSupplyUsage.getCreatedAt());
        deviceSupplyUsageDTO.setUpdatedAt(deviceSupplyUsage.getUpdatedAt());
        deviceSupplyUsageDTO.setCreatedBy(deviceSupplyUsage.getCreatedBy());
        deviceSupplyUsageDTO.setDevice(deviceSupplyUsage.getDevice() == null ? null : deviceSupplyUsage.getDevice().getId());
        deviceSupplyUsageDTO.setSupply(deviceSupplyUsage.getSupply() == null ? null : deviceSupplyUsage.getSupply().getId());
        return deviceSupplyUsageDTO;
    }

    private DeviceSupplyUsage mapToEntity(final DeviceSupplyUsageDTO deviceSupplyUsageDTO,
            final DeviceSupplyUsage deviceSupplyUsage) {
        deviceSupplyUsage.setQuantityUsed(deviceSupplyUsageDTO.getQuantityUsed());
        deviceSupplyUsage.setUsageDate(deviceSupplyUsageDTO.getUsageDate());
        deviceSupplyUsage.setNote(deviceSupplyUsageDTO.getNote());
        deviceSupplyUsage.setCreatedAt(deviceSupplyUsageDTO.getCreatedAt());
        deviceSupplyUsage.setUpdatedAt(deviceSupplyUsageDTO.getUpdatedAt());
        deviceSupplyUsage.setCreatedBy(deviceSupplyUsageDTO.getCreatedBy());
        final Device device = deviceSupplyUsageDTO.getDevice() == null ? null : deviceRepository.findById(deviceSupplyUsageDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        deviceSupplyUsage.setDevice(device);
        final Supply supply = deviceSupplyUsageDTO.getSupply() == null ? null : supplyRepository.findById(deviceSupplyUsageDTO.getSupply())
                .orElseThrow(() -> new NotFoundException("supply not found"));
        deviceSupplyUsage.setSupply(supply);
        return deviceSupplyUsage;
    }

}
