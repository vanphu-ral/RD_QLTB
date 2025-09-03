package io.qltb.qltb.service;

import io.qltb.qltb.domain.Criterial;
import io.qltb.qltb.domain.SampleReport;
import io.qltb.qltb.events.BeforeDeleteCriterial;
import io.qltb.qltb.events.BeforeDeleteSampleReport;
import io.qltb.qltb.model.CriterialDTO;
import io.qltb.qltb.repos.CriterialRepository;
import io.qltb.qltb.repos.SampleReportRepository;
import io.qltb.qltb.util.NotFoundException;
import io.qltb.qltb.util.ReferencedException;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class CriterialService {

    private final CriterialRepository criterialRepository;
    private final SampleReportRepository sampleReportRepository;
    private final ApplicationEventPublisher publisher;

    public CriterialService(final CriterialRepository criterialRepository,
            final SampleReportRepository sampleReportRepository,
            final ApplicationEventPublisher publisher) {
        this.criterialRepository = criterialRepository;
        this.sampleReportRepository = sampleReportRepository;
        this.publisher = publisher;
    }

    public List<CriterialDTO> findAll() {
        final List<Criterial> criterials = criterialRepository.findAll(Sort.by("id"));
        return criterials.stream()
                .map(criterial -> mapToDTO(criterial, new CriterialDTO()))
                .toList();
    }

    public CriterialDTO get(final Long id) {
        return criterialRepository.findById(id)
                .map(criterial -> mapToDTO(criterial, new CriterialDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final CriterialDTO criterialDTO) {
        final Criterial criterial = new Criterial();
        mapToEntity(criterialDTO, criterial);
        return criterialRepository.save(criterial).getId();
    }

    public void update(final Long id, final CriterialDTO criterialDTO) {
        final Criterial criterial = criterialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(criterialDTO, criterial);
        criterialRepository.save(criterial);
    }

    public void delete(final Long id) {
        final Criterial criterial = criterialRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        publisher.publishEvent(new BeforeDeleteCriterial(id));
        criterialRepository.delete(criterial);
    }

    private CriterialDTO mapToDTO(final Criterial criterial, final CriterialDTO criterialDTO) {
        criterialDTO.setId(criterial.getId());
        criterialDTO.setCode(criterial.getCode());
        criterialDTO.setName(criterial.getName());
        criterialDTO.setDetail(criterial.getDetail());
        criterialDTO.setDescription(criterial.getDescription());
        criterialDTO.setFrequency(criterial.getFrequency());
        criterialDTO.setCreatedAt(criterial.getCreatedAt());
        criterialDTO.setUpdatedAt(criterial.getUpdatedAt());
        criterialDTO.setCreatedBy(criterial.getCreatedBy());
        criterialDTO.setUpdatedBy(criterial.getUpdatedBy());
        criterialDTO.setStatus(criterial.getStatus());
        criterialDTO.setSampleReport(criterial.getSampleReport() == null ? null : criterial.getSampleReport());
        return criterialDTO;
    }

    private Criterial mapToEntity(final CriterialDTO criterialDTO, final Criterial criterial) {
        criterial.setCode(criterialDTO.getCode());
        criterial.setName(criterialDTO.getName());
        criterial.setDetail(criterialDTO.getDetail());
        criterial.setDescription(criterialDTO.getDescription());
        criterial.setFrequency(criterialDTO.getFrequency());
        criterial.setCreatedAt(criterialDTO.getCreatedAt());
        criterial.setUpdatedAt(criterialDTO.getUpdatedAt());
        criterial.setCreatedBy(criterialDTO.getCreatedBy());
        criterial.setUpdatedBy(criterialDTO.getUpdatedBy());
        criterial.setStatus(criterialDTO.getStatus());
        final SampleReport sampleReport = criterialDTO.getSampleReport() == null ? null : sampleReportRepository.findById(criterialDTO.getSampleReport().getId())
                .orElseThrow(() -> new NotFoundException("sampleReport not found"));
        criterial.setSampleReport(sampleReport);
        return criterial;
    }

    @EventListener(BeforeDeleteSampleReport.class)
    public void on(final BeforeDeleteSampleReport event) {
        final ReferencedException referencedException = new ReferencedException();
        final Criterial sampleReportCriterial = criterialRepository.findFirstBySampleReportId(event.getId());
        if (sampleReportCriterial != null) {
            referencedException.setKey("sampleReport.criterial.sampleReport.referenced");
            referencedException.addParam(sampleReportCriterial.getId());
            throw referencedException;
        }
    }

}
