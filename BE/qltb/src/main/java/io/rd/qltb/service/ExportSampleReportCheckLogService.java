package io.rd.qltb.service;

import io.rd.qltb.domain.ExportSampleReportCheckLog;
import io.rd.qltb.model.ExportSampleReportCheckLogDTO;
import io.rd.qltb.repos.ExportSampleReportCheckLogRepository;
import io.rd.qltb.util.NotFoundException;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class ExportSampleReportCheckLogService {

    private final ExportSampleReportCheckLogRepository exportSampleReportCheckLogRepository;

    public ExportSampleReportCheckLogService(
            final ExportSampleReportCheckLogRepository exportSampleReportCheckLogRepository) {
        this.exportSampleReportCheckLogRepository = exportSampleReportCheckLogRepository;
    }

    public List<ExportSampleReportCheckLogDTO> findAll() {
        final List<ExportSampleReportCheckLog> exportSampleReportCheckLogs = exportSampleReportCheckLogRepository.findAll(Sort.by("id"));
        return exportSampleReportCheckLogs.stream()
                .map(exportSampleReportCheckLog -> mapToDTO(exportSampleReportCheckLog, new ExportSampleReportCheckLogDTO()))
                .toList();
    }

    public ExportSampleReportCheckLogDTO get(final Long id) {
        return exportSampleReportCheckLogRepository.findById(id)
                .map(exportSampleReportCheckLog -> mapToDTO(exportSampleReportCheckLog, new ExportSampleReportCheckLogDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final ExportSampleReportCheckLogDTO exportSampleReportCheckLogDTO) {
        final ExportSampleReportCheckLog exportSampleReportCheckLog = new ExportSampleReportCheckLog();
        mapToEntity(exportSampleReportCheckLogDTO, exportSampleReportCheckLog);
        return exportSampleReportCheckLogRepository.save(exportSampleReportCheckLog).getId();
    }

    public void update(final Long id,
                       final ExportSampleReportCheckLogDTO exportSampleReportCheckLogDTO) {
        final ExportSampleReportCheckLog exportSampleReportCheckLog = exportSampleReportCheckLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(exportSampleReportCheckLogDTO, exportSampleReportCheckLog);
        exportSampleReportCheckLogRepository.save(exportSampleReportCheckLog);
    }

    public void delete(final Long id) {
        final ExportSampleReportCheckLog exportSampleReportCheckLog = exportSampleReportCheckLogRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        exportSampleReportCheckLogRepository.delete(exportSampleReportCheckLog);
    }

    private ExportSampleReportCheckLogDTO mapToDTO(
            final ExportSampleReportCheckLog exportSampleReportCheckLog,
            final ExportSampleReportCheckLogDTO exportSampleReportCheckLogDTO) {
        exportSampleReportCheckLogDTO.setId(exportSampleReportCheckLog.getId());
        exportSampleReportCheckLogDTO.setUsername(exportSampleReportCheckLog.getUsername());
        exportSampleReportCheckLogDTO.setDateExport(exportSampleReportCheckLog.getDateExport());
        exportSampleReportCheckLogDTO.setData(exportSampleReportCheckLog.getData());
        exportSampleReportCheckLogDTO.setFile(exportSampleReportCheckLog.getFile());
        return exportSampleReportCheckLogDTO;
    }

    private ExportSampleReportCheckLog mapToEntity(
            final ExportSampleReportCheckLogDTO exportSampleReportCheckLogDTO,
            final ExportSampleReportCheckLog exportSampleReportCheckLog) {
        exportSampleReportCheckLog.setUsername(exportSampleReportCheckLogDTO.getUsername());
        exportSampleReportCheckLog.setDateExport(exportSampleReportCheckLogDTO.getDateExport());
        exportSampleReportCheckLog.setData(exportSampleReportCheckLogDTO.getData());
        exportSampleReportCheckLog.setFile(exportSampleReportCheckLogDTO.getFile());
        return exportSampleReportCheckLog;
    }

}
