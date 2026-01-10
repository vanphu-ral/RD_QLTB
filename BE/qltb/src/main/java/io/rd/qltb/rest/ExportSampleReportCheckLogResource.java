package io.rd.qltb.rest;

import io.rd.qltb.domain.ExportSampleReportCheckLog;
import io.rd.qltb.model.ExportSampleReportCheckLogDTO;
import io.rd.qltb.repos.ExportSampleReportCheckLogRepository;
import io.rd.qltb.service.ExportSampleReportCheckLogService;
import jakarta.validation.Valid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping(value = "/api/exportSampleReportCheckLogs")
public class ExportSampleReportCheckLogResource {

    private final ExportSampleReportCheckLogService exportSampleReportCheckLogService;
    private final ExportSampleReportCheckLogRepository exportSampleReportCheckLogRepository;

    public ExportSampleReportCheckLogResource(
            final ExportSampleReportCheckLogService exportSampleReportCheckLogService,
            final ExportSampleReportCheckLogRepository exportSampleReportCheckLogRepository) {
        this.exportSampleReportCheckLogService = exportSampleReportCheckLogService;
        this.exportSampleReportCheckLogRepository = exportSampleReportCheckLogRepository;
    }

    @GetMapping
    public ResponseEntity<List<ExportSampleReportCheckLogDTO>> getAllExportSampleReportCheckLogs() {
        return ResponseEntity.ok(exportSampleReportCheckLogService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExportSampleReportCheckLogDTO> getExportSampleReportCheckLog(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(exportSampleReportCheckLogService.get(id));
    }

    @PostMapping
    public ResponseEntity<Long> createExportSampleReportCheckLog(
            @RequestBody @Valid final ExportSampleReportCheckLogDTO exportSampleReportCheckLogDTO) {
        final Long createdId = exportSampleReportCheckLogService.create(exportSampleReportCheckLogDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateExportSampleReportCheckLog(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final ExportSampleReportCheckLogDTO exportSampleReportCheckLogDTO) {
        exportSampleReportCheckLogService.update(id, exportSampleReportCheckLogDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExportSampleReportCheckLog(
            @PathVariable(name = "id") final Long id) {
        exportSampleReportCheckLogService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload-pdf")
    public ResponseEntity<?> uploadPdf(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        ExportSampleReportCheckLog log = exportSampleReportCheckLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        if (!"application/pdf".equals(file.getContentType())) {
            return ResponseEntity.badRequest().body("Only PDF allowed");
        }

        String uploadDir = "uploads/export-reports/";
        Files.createDirectories(Paths.get(uploadDir));

        String fileName = id + "_" + System.currentTimeMillis() + ".pdf";
        Path filePath = Paths.get(uploadDir, fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // update field file
        log.setFile(filePath.toString());
        exportSampleReportCheckLogRepository.save(log);

        return ResponseEntity.ok("Upload success");
    }

    @GetMapping("/{id}/view-pdf")
    public ResponseEntity<Resource> viewPdf(@PathVariable Long id) throws IOException {

        ExportSampleReportCheckLog log = exportSampleReportCheckLogRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if (log.getFile() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(log.getFile());
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filePath.getFileName().toString() + "\""
                )
                .body(resource);
    }



}