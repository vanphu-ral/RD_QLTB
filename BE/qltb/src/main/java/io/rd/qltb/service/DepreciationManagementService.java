package io.rd.qltb.service;

import io.rd.qltb.domain.DepreciationManagement;
import io.rd.qltb.model.DepreciationManagementDTO;
import io.rd.qltb.repos.DepreciationManagementRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class DepreciationManagementService {

    private final DepreciationManagementRepository depreciationManagementRepository;

    public DepreciationManagementService(
            final DepreciationManagementRepository depreciationManagementRepository) {
        this.depreciationManagementRepository = depreciationManagementRepository;
    }

    public List<DepreciationManagementDTO> findAll() {
        final List<DepreciationManagement> depreciationManagements = depreciationManagementRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
        final DepreciationManagement depreciationManagement = depreciationManagementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        depreciationManagementRepository.delete(depreciationManagement);
    }

    private DepreciationManagementDTO mapToDTO(final DepreciationManagement depreciationManagement,
            final DepreciationManagementDTO depreciationManagementDTO) {
        depreciationManagementDTO.setId(depreciationManagement.getId());
        depreciationManagementDTO.setCode(depreciationManagement.getCode());
        depreciationManagementDTO.setName(depreciationManagement.getName());
        depreciationManagementDTO.setDepr(depreciationManagement.getDepr());
        depreciationManagementDTO.setDeviceId(depreciationManagement.getDeviceId());
        depreciationManagementDTO.setCreatedAt(depreciationManagement.getCreatedAt());
        depreciationManagementDTO.setUpdatedAt(depreciationManagement.getUpdatedAt());
        depreciationManagementDTO.setCreatedBy(depreciationManagement.getCreatedBy());
        depreciationManagementDTO.setUpdatedBy(depreciationManagement.getUpdatedBy());
        depreciationManagementDTO.setStatus(depreciationManagement.getStatus());
        return depreciationManagementDTO;
    }

    private DepreciationManagement mapToEntity(
            final DepreciationManagementDTO depreciationManagementDTO,
            final DepreciationManagement depreciationManagement) {
        depreciationManagement.setCode(depreciationManagementDTO.getCode());
        depreciationManagement.setName(depreciationManagementDTO.getName());
        depreciationManagement.setDepr(depreciationManagementDTO.getDepr());
        depreciationManagement.setDeviceId(depreciationManagementDTO.getDeviceId());
        depreciationManagement.setCreatedAt(depreciationManagementDTO.getCreatedAt());
        depreciationManagement.setUpdatedAt(depreciationManagementDTO.getUpdatedAt());
        depreciationManagement.setCreatedBy(depreciationManagementDTO.getCreatedBy());
        depreciationManagement.setUpdatedBy(depreciationManagementDTO.getUpdatedBy());
        depreciationManagement.setStatus(depreciationManagementDTO.getStatus());
        return depreciationManagement;
    }

}
