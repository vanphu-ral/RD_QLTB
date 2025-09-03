package io.qltb.qltb.service;

import io.qltb.qltb.domain.Device;
import io.qltb.qltb.domain.PerformanceManagement;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.model.PerformanceManagementDTO;
import io.qltb.qltb.repos.DeviceRepository;
import io.qltb.qltb.repos.PerformanceManagementRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


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
        performanceManagementDTO.setCreatedAt(performanceManagement.getCreatedAt());
        performanceManagementDTO.setUpdatedAt(performanceManagement.getUpdatedAt());
        performanceManagementDTO.setCreatedBy(performanceManagement.getCreatedBy());
        performanceManagementDTO.setUpdatedBy(performanceManagement.getUpdatedBy());
        performanceManagementDTO.setStatus(performanceManagement.getStatus());
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
        performanceManagement.setUpdatedBy(performanceManagementDTO.getUpdatedBy());
        performanceManagement.setStatus(performanceManagementDTO.getStatus());
        final Device device = performanceManagementDTO.getDevice() == null ? null : deviceRepository.findById(performanceManagementDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        performanceManagement.setDevice(device);
        return performanceManagement;
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final PerformanceManagement devicePerformanceManagement = performanceManagementRepository.findFirstByDeviceId(event.getId());
        if (devicePerformanceManagement != null) {
            referencedException.setKey("device.performanceManagement.device.referenced");
            referencedException.addParam(devicePerformanceManagement.getId());
            throw referencedException;
        }
    }

}
