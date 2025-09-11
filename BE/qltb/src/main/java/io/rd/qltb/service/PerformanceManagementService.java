package io.rd.qltb.service;

import io.rd.qltb.domain.PerformanceManagement;
import io.rd.qltb.model.PerformanceManagementDTO;
import io.rd.qltb.repos.PerformanceManagementRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PerformanceManagementService {

    private final PerformanceManagementRepository performanceManagementRepository;

    public PerformanceManagementService(
            final PerformanceManagementRepository performanceManagementRepository) {
        this.performanceManagementRepository = performanceManagementRepository;
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
        final PerformanceManagement performanceManagement = performanceManagementRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        performanceManagementRepository.delete(performanceManagement);
    }

    private PerformanceManagementDTO mapToDTO(final PerformanceManagement performanceManagement,
            final PerformanceManagementDTO performanceManagementDTO) {
        performanceManagementDTO.setId(performanceManagement.getId());
        performanceManagementDTO.setCode(performanceManagement.getCode());
        performanceManagementDTO.setName(performanceManagement.getName());
        performanceManagementDTO.setPerformance(performanceManagement.getPerformance());
        performanceManagementDTO.setDeviceId(performanceManagement.getDeviceId());
        performanceManagementDTO.setCreatedAt(performanceManagement.getCreatedAt());
        performanceManagementDTO.setUpdatedAt(performanceManagement.getUpdatedAt());
        performanceManagementDTO.setCreatedBy(performanceManagement.getCreatedBy());
        performanceManagementDTO.setUpdatedBy(performanceManagement.getUpdatedBy());
        performanceManagementDTO.setStatus(performanceManagement.getStatus());
        return performanceManagementDTO;
    }

    private PerformanceManagement mapToEntity(
            final PerformanceManagementDTO performanceManagementDTO,
            final PerformanceManagement performanceManagement) {
        performanceManagement.setCode(performanceManagementDTO.getCode());
        performanceManagement.setName(performanceManagementDTO.getName());
        performanceManagement.setPerformance(performanceManagementDTO.getPerformance());
        performanceManagement.setDeviceId(performanceManagementDTO.getDeviceId());
        performanceManagement.setCreatedAt(performanceManagementDTO.getCreatedAt());
        performanceManagement.setUpdatedAt(performanceManagementDTO.getUpdatedAt());
        performanceManagement.setCreatedBy(performanceManagementDTO.getCreatedBy());
        performanceManagement.setUpdatedBy(performanceManagementDTO.getUpdatedBy());
        performanceManagement.setStatus(performanceManagementDTO.getStatus());
        return performanceManagement;
    }

}
