package io.rd.qltb.service;

import io.rd.qltb.domain.Criterial;
import io.rd.qltb.domain.KeyMapping;
import io.rd.qltb.domain.SampleReport;
import io.rd.qltb.events.BeforeDeleteCriterial;
import io.rd.qltb.events.BeforeDeleteSampleReport;
import io.rd.qltb.model.KeyMappingDTO;
import io.rd.qltb.repos.CriterialRepository;
import io.rd.qltb.repos.KeyMappingRepository;
import io.rd.qltb.repos.SampleReportRepository;
import io.rd.qltb.util.NotFoundException;
import io.rd.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class KeyMappingService {

    private final KeyMappingRepository keyMappingRepository;
    private final SampleReportRepository sampleReportRepository;
    private final CriterialRepository criterialRepository;

    public KeyMappingService(final KeyMappingRepository keyMappingRepository,
            final SampleReportRepository sampleReportRepository,
            final CriterialRepository criterialRepository) {
        this.keyMappingRepository = keyMappingRepository;
        this.sampleReportRepository = sampleReportRepository;
        this.criterialRepository = criterialRepository;
    }

    public List<KeyMappingDTO> findAll() {
        final List<KeyMapping> keyMappings = keyMappingRepository.findAll(Sort.by("id"));
        return keyMappings.stream()
                .map(keyMapping -> mapToDTO(keyMapping, new KeyMappingDTO()))
                .toList();
    }

    public KeyMappingDTO get(final Integer id) {
        return keyMappingRepository.findById(id)
                .map(keyMapping -> mapToDTO(keyMapping, new KeyMappingDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final KeyMappingDTO keyMappingDTO) {
        final KeyMapping keyMapping = new KeyMapping();
        mapToEntity(keyMappingDTO, keyMapping);
        return keyMappingRepository.save(keyMapping).getId();
    }

    public void update(final Integer id, final KeyMappingDTO keyMappingDTO) {
        final KeyMapping keyMapping = keyMappingRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(keyMappingDTO, keyMapping);
        keyMappingRepository.save(keyMapping);
    }

    public void delete(final Integer id) {
        final KeyMapping keyMapping = keyMappingRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        keyMappingRepository.delete(keyMapping);
    }

    private KeyMappingDTO mapToDTO(final KeyMapping keyMapping, final KeyMappingDTO dto) {
        dto.setId(keyMapping.getId());
        dto.setCriterialGroupId(keyMapping.getCriterialGroupId());

        // Sao chép SampleReport có kiểm soát
        if (keyMapping.getSampleReport() != null) {
            SampleReport sampleReportCopy = new SampleReport();
            sampleReportCopy.setId(keyMapping.getSampleReport().getId());
            sampleReportCopy.setCode(keyMapping.getSampleReport().getCode());
            sampleReportCopy.setName(keyMapping.getSampleReport().getName());
            sampleReportCopy.setFrequency(keyMapping.getSampleReport().getFrequency());
            sampleReportCopy.setType(keyMapping.getSampleReport().getType());
            sampleReportCopy.setCreatedAt(keyMapping.getSampleReport().getCreatedAt());
            sampleReportCopy.setUpdatedAt(keyMapping.getSampleReport().getUpdatedAt());
            sampleReportCopy.setCreatedBy(keyMapping.getSampleReport().getCreatedBy());
            sampleReportCopy.setUpdatedBy(keyMapping.getSampleReport().getUpdatedBy());
            sampleReportCopy.setStatus(keyMapping.getSampleReport().getStatus());

            // Xóa các quan hệ con
            sampleReportCopy.setDeviceGroup(null);
            sampleReportCopy.setSampleReportKeyMappingDeviceSampleReports(null);
            sampleReportCopy.setSampleReportKeyMappings(null);

            dto.setSampleReport(sampleReportCopy);
        } else {
            dto.setSampleReport(null);
        }

        // Sao chép Criterial có kiểm soát
        if (keyMapping.getCriterial() != null) {
            Criterial criterialCopy = new Criterial();
            criterialCopy.setId(keyMapping.getCriterial().getId());
            criterialCopy.setCode(keyMapping.getCriterial().getCode());
            criterialCopy.setName(keyMapping.getCriterial().getName());
            criterialCopy.setDetail(keyMapping.getCriterial().getDetail());
            criterialCopy.setDescription(keyMapping.getCriterial().getDescription());
            criterialCopy.setFrequency(keyMapping.getCriterial().getFrequency());
            criterialCopy.setCreatedAt(keyMapping.getCriterial().getCreatedAt());
            criterialCopy.setUpdatedAt(keyMapping.getCriterial().getUpdatedAt());
            criterialCopy.setCreatedBy(keyMapping.getCriterial().getCreatedBy());
            criterialCopy.setUpdatedBy(keyMapping.getCriterial().getUpdatedBy());
            criterialCopy.setStatus(keyMapping.getCriterial().getStatus());

            // Xóa các quan hệ con
            criterialCopy.setCriterialGroup(null);
            criterialCopy.setCriterialKeyMappings(null);

            dto.setCriterial(criterialCopy);
        } else {
            dto.setCriterial(null);
        }

        return dto;
    }


    private KeyMapping mapToEntity(final KeyMappingDTO keyMappingDTO, final KeyMapping keyMapping) {
        keyMapping.setCriterialGroupId(keyMappingDTO.getCriterialGroupId());
        final SampleReport sampleReport = keyMappingDTO.getSampleReport() == null ? null : sampleReportRepository.findById(keyMappingDTO.getSampleReport().getId())
                .orElseThrow(() -> new NotFoundException("sampleReport not found"));
        keyMapping.setSampleReport(sampleReport);
        final Criterial criterial = keyMappingDTO.getCriterial() == null ? null : criterialRepository.findById(keyMappingDTO.getCriterial().getId())
                .orElseThrow(() -> new NotFoundException("criterial not found"));
        keyMapping.setCriterial(criterial);
        return keyMapping;
    }

    @EventListener(BeforeDeleteSampleReport.class)
    public void on(final BeforeDeleteSampleReport event) {
        final ReferencedException referencedException = new ReferencedException();
        final KeyMapping sampleReportKeyMapping = keyMappingRepository.findFirstBySampleReportId(event.getId());
        if (sampleReportKeyMapping != null) {
            referencedException.setKey("sampleReport.keyMapping.sampleReport.referenced");
            referencedException.addParam(sampleReportKeyMapping.getId());
            throw referencedException;
        }
    }

    @EventListener(BeforeDeleteCriterial.class)
    public void on(final BeforeDeleteCriterial event) {
        final ReferencedException referencedException = new ReferencedException();
        final KeyMapping criterialKeyMapping = keyMappingRepository.findFirstByCriterialId(event.getId());
        if (criterialKeyMapping != null) {
            referencedException.setKey("criterial.keyMapping.criterial.referenced");
            referencedException.addParam(criterialKeyMapping.getId());
            throw referencedException;
        }
    }

}
