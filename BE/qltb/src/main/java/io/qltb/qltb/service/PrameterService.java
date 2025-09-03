package io.qltb.qltb.service;

import io.qltb.qltb.domain.Device;
import io.qltb.qltb.domain.Prameter;
import io.qltb.qltb.domain.PrameterGroup;
import io.qltb.qltb.events.BeforeDeleteDevice;
import io.qltb.qltb.events.BeforeDeletePrameterGroup;
import io.qltb.qltb.model.PrameterDTO;
import io.qltb.qltb.repos.DeviceRepository;
import io.qltb.qltb.repos.PrameterGroupRepository;
import io.qltb.qltb.repos.PrameterRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PrameterService {

    private final PrameterRepository prameterRepository;
    private final PrameterGroupRepository prameterGroupRepository;
    private final DeviceRepository deviceRepository;

    public PrameterService(final PrameterRepository prameterRepository,
            final PrameterGroupRepository prameterGroupRepository,
            final DeviceRepository deviceRepository) {
        this.prameterRepository = prameterRepository;
        this.prameterGroupRepository = prameterGroupRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<PrameterDTO> findAll() {
        final List<Prameter> prameters = prameterRepository.findAll(Sort.by("id"));
        return prameters.stream()
                .map(prameter -> mapToDTO(prameter, new PrameterDTO()))
                .toList();
    }

    public PrameterDTO get(final Long id) {
        return prameterRepository.findById(id)
                .map(prameter -> mapToDTO(prameter, new PrameterDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final PrameterDTO prameterDTO) {
        final Prameter prameter = new Prameter();
        mapToEntity(prameterDTO, prameter);
        return prameterRepository.save(prameter).getId();
    }

    public void update(final Long id, final PrameterDTO prameterDTO) {
        final Prameter prameter = prameterRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(prameterDTO, prameter);
        prameterRepository.save(prameter);
    }

    public void delete(final Long id) {
        final Prameter prameter = prameterRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        prameterRepository.delete(prameter);
    }

    private PrameterDTO mapToDTO(final Prameter prameter, final PrameterDTO prameterDTO) {
        prameterDTO.setId(prameter.getId());
        prameterDTO.setCode(prameter.getCode());
        prameterDTO.setName(prameter.getName());
        prameterDTO.setValue(prameter.getValue());
        prameterDTO.setMin(prameter.getMin());
        prameterDTO.setMax(prameter.getMax());
        prameterDTO.setUnit(prameter.getUnit());
        prameterDTO.setDescription(prameter.getDescription());
        prameterDTO.setCreatedAt(prameter.getCreatedAt());
        prameterDTO.setUpdatedAt(prameter.getUpdatedAt());
        prameterDTO.setCreatedBy(prameter.getCreatedBy());
        prameterDTO.setUpdatedBy(prameter.getUpdatedBy());
        prameterDTO.setStatus(prameter.getStatus());
        prameterDTO.setParameterGroup(prameter.getParameterGroup() == null ? null : prameter.getParameterGroup().getId());
        prameterDTO.setDevice(prameter.getDevice() == null ? null : prameter.getDevice().getId());
        return prameterDTO;
    }

    private Prameter mapToEntity(final PrameterDTO prameterDTO, final Prameter prameter) {
        prameter.setCode(prameterDTO.getCode());
        prameter.setName(prameterDTO.getName());
        prameter.setValue(prameterDTO.getValue());
        prameter.setMin(prameterDTO.getMin());
        prameter.setMax(prameterDTO.getMax());
        prameter.setUnit(prameterDTO.getUnit());
        prameter.setDescription(prameterDTO.getDescription());
        prameter.setCreatedAt(prameterDTO.getCreatedAt());
        prameter.setUpdatedAt(prameterDTO.getUpdatedAt());
        prameter.setCreatedBy(prameterDTO.getCreatedBy());
        prameter.setUpdatedBy(prameterDTO.getUpdatedBy());
        prameter.setStatus(prameterDTO.getStatus());
        final PrameterGroup parameterGroup = prameterDTO.getParameterGroup() == null ? null : prameterGroupRepository.findById(prameterDTO.getParameterGroup())
                .orElseThrow(() -> new NotFoundException("parameterGroup not found"));
        prameter.setParameterGroup(parameterGroup);
        final Device device = prameterDTO.getDevice() == null ? null : deviceRepository.findById(prameterDTO.getDevice())
                .orElseThrow(() -> new NotFoundException("device not found"));
        prameter.setDevice(device);
        return prameter;
    }

    @EventListener(BeforeDeletePrameterGroup.class)
    public void on(final BeforeDeletePrameterGroup event) {
        final ReferencedException referencedException = new ReferencedException();
        final Prameter parameterGroupPrameter = prameterRepository.findFirstByParameterGroupId(event.getId());
        if (parameterGroupPrameter != null) {
            referencedException.setKey("prameterGroup.prameter.parameterGroup.referenced");
            referencedException.addParam(parameterGroupPrameter.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteDevice.class)
    public void on(final BeforeDeleteDevice event) {
        final ReferencedException referencedException = new ReferencedException();
        final Prameter devicePrameter = prameterRepository.findFirstByDeviceId(event.getId());
        if (devicePrameter != null) {
            referencedException.setKey("device.prameter.device.referenced");
            referencedException.addParam(devicePrameter.getId());
            throw referencedException;
        }
    }

}
