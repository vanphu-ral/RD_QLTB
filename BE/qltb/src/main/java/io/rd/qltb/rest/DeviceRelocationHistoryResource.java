package io.rd.qltb.rest;

import io.rd.qltb.model.DeviceRelocationHistoryDTO;
import io.rd.qltb.model.DeviceRelocationHistoryViewDTO;
import io.rd.qltb.service.DeviceRelocationHistoryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/deviceRelocationHistories", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceRelocationHistoryResource {

    private final DeviceRelocationHistoryService deviceRelocationHistoryService;

    public DeviceRelocationHistoryResource(
            final DeviceRelocationHistoryService deviceRelocationHistoryService) {
        this.deviceRelocationHistoryService = deviceRelocationHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceRelocationHistoryDTO>> getAllDeviceRelocationHistories() {
        return ResponseEntity.ok(deviceRelocationHistoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceRelocationHistoryDTO> getDeviceRelocationHistory(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(deviceRelocationHistoryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDeviceRelocationHistory(
            @RequestBody @Valid final DeviceRelocationHistoryDTO deviceRelocationHistoryDTO) {
        final Long createdId = deviceRelocationHistoryService.create(deviceRelocationHistoryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDeviceRelocationHistory(
            @PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DeviceRelocationHistoryDTO deviceRelocationHistoryDTO) {
        deviceRelocationHistoryService.update(id, deviceRelocationHistoryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDeviceRelocationHistory(
            @PathVariable(name = "id") final Long id) {
        deviceRelocationHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<DeviceRelocationHistoryViewDTO>> getAllByDeviceId(
            @PathVariable(name = "deviceId") final Long deviceId) {
        return ResponseEntity.ok(deviceRelocationHistoryService.findAllByDeviceId(deviceId));
    }

}
