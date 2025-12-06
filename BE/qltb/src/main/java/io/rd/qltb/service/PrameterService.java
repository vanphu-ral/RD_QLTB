package io.rd.qltb.service;

import io.rd.qltb.config.GlobalConfig;
import io.rd.qltb.domain.Prameter;
import io.rd.qltb.domain.PrameterGroup;
import io.rd.qltb.events.BeforeDeletePrameterGroup;
import io.rd.qltb.model.PrameterDTO;
import io.rd.qltb.repos.DeviceRepository;
import io.rd.qltb.repos.PrameterGroupRepository;
import io.rd.qltb.repos.PrameterRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;

import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class PrameterService {

    private final PrameterRepository prameterRepository;
    private final PrameterGroupRepository prameterGroupRepository;
    private final DeviceRepository deviceRepository;
    private final GlobalConfig globalConfig;

    public PrameterService(final PrameterRepository prameterRepository,
                           final PrameterGroupRepository prameterGroupRepository,
                           final DeviceRepository deviceRepository, GlobalConfig globalConfig) {
        this.prameterRepository = prameterRepository;
        this.prameterGroupRepository = prameterGroupRepository;
        this.deviceRepository = deviceRepository;
        this.globalConfig = globalConfig;
    }

    public List<PrameterDTO> findAll() {
        final List<Prameter> prameters = prameterRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
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
        Prameter savedPrameter = prameterRepository.save(prameter);
        savedPrameter.setCode(prameterDTO.getCode()+"-"+globalConfig.createNumberPrefix(savedPrameter.getId(),6));
        return prameterRepository.save(savedPrameter).getId();
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

    private PrameterDTO mapToDTO(final Prameter prameter, final PrameterDTO dto) {
        dto.setId(prameter.getId());
        dto.setCode(prameter.getCode());
        dto.setName(prameter.getName());
        dto.setDescription(prameter.getDescription());
        dto.setCreatedAt(prameter.getCreatedAt());
        dto.setUpdatedAt(prameter.getUpdatedAt());
        dto.setCreatedBy(prameter.getCreatedBy());
        dto.setUpdatedBy(prameter.getUpdatedBy());
        dto.setStatus(prameter.getStatus());

        // Sao chép ParameterGroup có kiểm soát
        if (prameter.getParameterGroup() != null) {
            PrameterGroup groupCopy = new PrameterGroup();
            groupCopy.setId(prameter.getParameterGroup().getId());
            groupCopy.setCode(prameter.getParameterGroup().getCode());
            groupCopy.setName(prameter.getParameterGroup().getName());
            groupCopy.setDescription(prameter.getParameterGroup().getDescription());
            groupCopy.setCreatedAt(prameter.getParameterGroup().getCreatedAt());
            groupCopy.setUpdatedAt(prameter.getParameterGroup().getUpdatedAt());
            groupCopy.setCreatedBy(prameter.getParameterGroup().getCreatedBy());
            groupCopy.setUpdatedBy(prameter.getParameterGroup().getUpdatedBy());
            groupCopy.setStatus(prameter.getParameterGroup().getStatus());

            // Xóa các quan hệ con để tránh vòng lặp
            groupCopy.setParameterGroupPrameters(null);

            dto.setParameterGroup(groupCopy);
        } else {
            dto.setParameterGroup(null);
        }


        return dto;
    }


    private Prameter mapToEntity(final PrameterDTO prameterDTO, final Prameter prameter) {
        prameter.setCode(prameterDTO.getCode());
        prameter.setName(prameterDTO.getName());
        prameter.setDescription(prameterDTO.getDescription());
        prameter.setCreatedAt(prameterDTO.getCreatedAt());
        prameter.setUpdatedAt(prameterDTO.getUpdatedAt());
        prameter.setCreatedBy(prameterDTO.getCreatedBy());
        prameter.setUpdatedBy(prameterDTO.getUpdatedBy());
        prameter.setStatus(prameterDTO.getStatus());
        final PrameterGroup parameterGroup = prameterDTO.getParameterGroup() == null ? null : prameterGroupRepository.findById(prameterDTO.getParameterGroup().getId())
                .orElseThrow(() -> new NotFoundException("parameterGroup not found"));
        prameter.setParameterGroup(parameterGroup);
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


}
