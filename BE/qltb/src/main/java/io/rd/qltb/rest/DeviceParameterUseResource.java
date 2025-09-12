package io.rd.qltb.rest;

import io.rd.qltb.model.DeviceParameterUseDTO;
import io.rd.qltb.service.DeviceParameterUseService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/deviceParameterUses", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceParameterUseResource {

    private final DeviceParameterUseService deviceParameterUseService;

    public DeviceParameterUseResource(final DeviceParameterUseService deviceParameterUseService) {
        this.deviceParameterUseService = deviceParameterUseService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceParameterUseDTO>> getAllDeviceParameterUses() {
        return ResponseEntity.ok(deviceParameterUseService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceParameterUseDTO> getDeviceParameterUse(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(deviceParameterUseService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDeviceParameterUse(
            @RequestBody @Valid final DeviceParameterUseDTO deviceParameterUseDTO) {
        final Long createdId = deviceParameterUseService.create(deviceParameterUseDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDeviceParameterUse(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DeviceParameterUseDTO deviceParameterUseDTO) {
        deviceParameterUseService.update(id, deviceParameterUseDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDeviceParameterUse(@PathVariable(name = "id") final Long id) {
        deviceParameterUseService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
