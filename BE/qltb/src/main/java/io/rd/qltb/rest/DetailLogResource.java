package io.rd.qltb.rest;

import io.rd.qltb.model.DetailLogResponseDTO;
import io.rd.qltb.service.DetailLogService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
    @RequestMapping(value = "/api/detail-logs",produces = MediaType.APPLICATION_JSON_VALUE)
public class DetailLogResource {
    @Autowired
    private DetailLogService detailLogService;
    @GetMapping
    public ResponseEntity<DetailLogResponseDTO> getDetailLogs(
            @RequestParam String entityType,
            @RequestParam Long entityId) {
        DetailLogResponseDTO responseDTO = detailLogService.getAllByEntityTypeAndEntityId(entityType, entityId);
        return ResponseEntity.ok(responseDTO);
    }
}
