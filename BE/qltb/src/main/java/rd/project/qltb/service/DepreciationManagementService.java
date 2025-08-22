package rd.project.qltb.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rd.project.qltb.domain.DepreciationManagement;
import rd.project.qltb.domain.Device;
import rd.project.qltb.model.DepreciationManagementDTO;
import rd.project.qltb.repos.DepreciationManagementRepository;
import rd.project.qltb.repos.DeviceRepository;
import rd.project.qltb.util.NotFoundException;


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
        depreciationManagementRepository.deleteById(id);
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
        depreciationManagementDTO.setDevice(depreciationManagement.getDevice() == null ? null : depreciationManagement.getDevice().getId());
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
        final Device device = depreciationManagementDTO.getDevice() == null ? null : deviceRepository.findById(depreciationManagementDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        depreciationManagement.setDevice(device);
        return depreciationManagement;
    }

}
