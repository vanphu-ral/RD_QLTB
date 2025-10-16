package io.rd.qltb.rest;


import io.rd.qltb.model.DeviceCurrentSupplyDTO;
import io.rd.qltb.service.DeviceCurrentSupplyService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/deviceCurrentSupplies", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceCurrentSupplyResource {

    private final DeviceCurrentSupplyService deviceCurrentSupplyService;

    public DeviceCurrentSupplyResource(
            final DeviceCurrentSupplyService deviceCurrentSupplyService) {
        this.deviceCurrentSupplyService = deviceCurrentSupplyService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceCurrentSupplyDTO>> getAllDeviceCurrentSupplies() {
        return ResponseEntity.ok(deviceCurrentSupplyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceCurrentSupplyDTO> getDeviceCurrentSupply(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(deviceCurrentSupplyService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDeviceCurrentSupply(
            @RequestBody @Valid final DeviceCurrentSupplyDTO deviceCurrentSupplyDTO) {
        final Long createdId = deviceCurrentSupplyService.create(deviceCurrentSupplyDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDeviceCurrentSupply(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DeviceCurrentSupplyDTO deviceCurrentSupplyDTO) {
        deviceCurrentSupplyService.update(id, deviceCurrentSupplyDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDeviceCurrentSupply(
            @PathVariable(name = "id") final Long id) {
        deviceCurrentSupplyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
