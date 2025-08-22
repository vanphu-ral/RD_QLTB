package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.PerformanceManagement;
import rd.project.qltb.model.PerformanceManagementDTO;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.repos.PerformanceManagementRepository;
import rd.project.qltb.util.NotFoundException;


@Service
public class PerformanceManagementService {

    private final PerformanceManagementRepository performanceManagementRepository;
    private final DeviceRepository deviceRepository;

    public PerformanceManagementService(
            final PerformanceManagementRepository performanceManagementRepository,
            final DeviceRepository deviceRepository) {
        this.performanceManagementRepository = performanceManagementRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<PerformanceManagementDTO> findAll() {
        final List<PerformanceManagement> performanceManagements = performanceManagementRepository.findAll(Sort.by("id"));
        return performanceManagements.stream()
                .map(performanceManagement -> mapToDTO(performanceManagement, new PerformanceManagementDTO()))
                .toList();
    }

    public PerformanceManagementDTO get(final Long id) {
        return performanceManagementRepository.findById(id)
                .map(performanceManagement -> mapToDTO(performanceManagement, new PerformanceManagementDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PerformanceManagementDTO performanceManagementDTO) {
        final PerformanceManagement performanceManagement = new PerformanceManagement();
        mapToEntity(performanceManagementDTO, performanceManagement);
        return performanceManagementRepository.save(performanceManagement).getId();
    }

    public void update(final Long id, final PerformanceManagementDTO performanceManagementDTO) {
        final PerformanceManagement performanceManagement = performanceManagementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(performanceManagementDTO, performanceManagement);
        performanceManagementRepository.save(performanceManagement);
    }

    public void delete(final Long id) {
        performanceManagementRepository.deleteById(id);
    }

    private PerformanceManagementDTO mapToDTO(final PerformanceManagement performanceManagement,
            final PerformanceManagementDTO performanceManagementDTO) {
        performanceManagementDTO.setId(performanceManagement.getId());
        performanceManagementDTO.setCode(performanceManagement.getCode());
        performanceManagementDTO.setName(performanceManagement.getName());
        performanceManagementDTO.setPerformance(performanceManagement.getPerformance());
        performanceManagementDTO.setCreatedAt(performanceManagement.getCreatedAt());
        performanceManagementDTO.setUpdatedAt(performanceManagement.getUpdatedAt());
        performanceManagementDTO.setCreatedBy(performanceManagement.getCreatedBy());
        performanceManagementDTO.setDevice(performanceManagement.getDevice() == null ? null : performanceManagement.getDevice().getId());
        return performanceManagementDTO;
    }

    private PerformanceManagement mapToEntity(
            final PerformanceManagementDTO performanceManagementDTO,
            final PerformanceManagement performanceManagement) {
        performanceManagement.setCode(performanceManagementDTO.getCode());
        performanceManagement.setName(performanceManagementDTO.getName());
        performanceManagement.setPerformance(performanceManagementDTO.getPerformance());
        performanceManagement.setCreatedAt(performanceManagementDTO.getCreatedAt());
        performanceManagement.setUpdatedAt(performanceManagementDTO.getUpdatedAt());
        performanceManagement.setCreatedBy(performanceManagementDTO.getCreatedBy());
        final Device device = performanceManagementDTO.getDevice() == null ? null : deviceRepository.findById(performanceManagementDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        performanceManagement.setDevice(device);
        return performanceManagement;
    }

}
