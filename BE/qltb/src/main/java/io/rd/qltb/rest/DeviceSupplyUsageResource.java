package io.rd.qltb.rest;

import io.rd.qltb.model.DeviceSupplyUsageDTO;
import io.rd.qltb.model.SupplyDetailDTO;
import io.rd.qltb.service.DeviceSupplyUsageService;
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
@RequestMapping(value = "/api/deviceSupplyUsages", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceSupplyUsageResource {

    private final DeviceSupplyUsageService deviceSupplyUsageService;

    public DeviceSupplyUsageResource(final DeviceSupplyUsageService deviceSupplyUsageService) {
        this.deviceSupplyUsageService = deviceSupplyUsageService;
    }

    @GetMapping("/byDevice/{deviceId}")
    public ResponseEntity<List<DeviceSupplyUsageDTO>> getBySupply(@PathVariable Long deviceId) {
        return ResponseEntity.ok(deviceSupplyUsageService.getByDeviceId(deviceId));
    }

    @GetMapping
    public ResponseEntity<List<DeviceSupplyUsageDTO>> getAllDeviceSupplyUsages() {
        return ResponseEntity.ok(deviceSupplyUsageService.findAll());
    }
    @PostMapping("/creates")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<List<Long>> creates(@RequestBody @Valid List<DeviceSupplyUsageDTO> deviceSupplyUsageDTOS) {
        List<Long> createdIds = deviceSupplyUsageService.creates(deviceSupplyUsageDTOS);
        return new ResponseEntity<>(createdIds, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<DeviceSupplyUsageDTO> getDeviceSupplyUsage(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(deviceSupplyUsageService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDeviceSupplyUsage(
            @RequestBody @Valid final DeviceSupplyUsageDTO deviceSupplyUsageDTO) {
        final Long createdId = deviceSupplyUsageService.create(deviceSupplyUsageDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDeviceSupplyUsage(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DeviceSupplyUsageDTO deviceSupplyUsageDTO) {
        deviceSupplyUsageService.update(id, deviceSupplyUsageDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDeviceSupplyUsage(@PathVariable(name = "id") final Long id) {
        deviceSupplyUsageService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
