package io.qltb.qltb.service;

import io.qltb.qltb.domain.Criterial;
import io.qltb.qltb.domain.KeyMapping;
import io.qltb.qltb.domain.SampleReport;
import io.qltb.qltb.events.BeforeDeleteCriterial;
import io.qltb.qltb.events.BeforeDeleteSampleReport;
import io.qltb.qltb.model.KeyMappingDTO;
import io.qltb.qltb.repos.CriterialRepository;
import io.qltb.qltb.repos.KeyMappingRepository;
import io.qltb.qltb.repos.SampleReportRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
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

    public KeyMappingDTO get(final Long id) {
        return keyMappingRepository.findById(id)
                .map(keyMapping -> mapToDTO(keyMapping, new KeyMappingDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final KeyMappingDTO keyMappingDTO) {
        final KeyMapping keyMapping = new KeyMapping();
        mapToEntity(keyMappingDTO, keyMapping);
        return keyMappingRepository.save(keyMapping).getId();
    }

    public void update(final Long id, final KeyMappingDTO keyMappingDTO) {
        final KeyMapping keyMapping = keyMappingRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(keyMappingDTO, keyMapping);
        keyMappingRepository.save(keyMapping);
    }

    public void delete(final Long id) {
        final KeyMapping keyMapping = keyMappingRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        keyMappingRepository.delete(keyMapping);
    }

    private KeyMappingDTO mapToDTO(final KeyMapping keyMapping, final KeyMappingDTO keyMappingDTO) {
        keyMappingDTO.setId(keyMapping.getId());

        // Sao chép SampleReport
        if (keyMapping.getSampleReport() != null) {
            SampleReport sampleReportCopy = new SampleReport();
            sampleReportCopy.setId(keyMapping.getSampleReport().getId());
            sampleReportCopy.setCode(keyMapping.getSampleReport().getCode());
            sampleReportCopy.setName(keyMapping.getSampleReport().getName());
            sampleReportCopy.setStatus(keyMapping.getSampleReport().getStatus());
            sampleReportCopy.setCreatedAt(keyMapping.getSampleReport().getCreatedAt());
            sampleReportCopy.setUpdatedAt(keyMapping.getSampleReport().getUpdatedAt());
            sampleReportCopy.setCreatedBy(keyMapping.getSampleReport().getCreatedBy());
            sampleReportCopy.setUpdatedBy(keyMapping.getSampleReport().getUpdatedBy());

            // Xóa các quan hệ con
            sampleReportCopy.setDeviceGroup(null);
            sampleReportCopy.setSampleReportCriterials(null);
            sampleReportCopy.setSampleReportKeyMappings(null);
            sampleReportCopy.setSampleReportKeyMappingDeviceSampleReports(null);

            keyMappingDTO.setSampleReport(sampleReportCopy);
        } else {
            keyMappingDTO.setSampleReport(null);
        }

        // Sao chép Criterial
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
            criterialCopy.setSampleReport(null);
            criterialCopy.setCriterialKeyMappings(null);

            keyMappingDTO.setCriterial(criterialCopy);
        } else {
            keyMappingDTO.setCriterial(null);
        }

        return keyMappingDTO;
    }


    private KeyMapping mapToEntity(final KeyMappingDTO keyMappingDTO, final KeyMapping keyMapping) {
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
            referencedException.setKey("keyMapping.keyMapping.keyMapping.referenced");
            referencedException.addParam(criterialKeyMapping.getId());
            throw referencedException;
        }
    }

}
