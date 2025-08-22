package rd.project.qltb.rest;

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
import rd.project.qltb.model.DeviceHistoryDTO;
import rd.project.qltb.service.DeviceHistoryService;


@RestController
@RequestMapping(value = "/api/deviceHistories", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceHistoryResource {

    private final DeviceHistoryService deviceHistoryService;

    public DeviceHistoryResource(final DeviceHistoryService deviceHistoryService) {
        this.deviceHistoryService = deviceHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceHistoryDTO>> getAllDeviceHistories() {
        return ResponseEntity.ok(deviceHistoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceHistoryDTO> getDeviceHistory(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(deviceHistoryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDeviceHistory(
            @RequestBody @Valid final DeviceHistoryDTO deviceHistoryDTO) {
        final Long createdId = deviceHistoryService.create(deviceHistoryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDeviceHistory(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DeviceHistoryDTO deviceHistoryDTO) {
        deviceHistoryService.update(id, deviceHistoryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDeviceHistory(@PathVariable(name = "id") final Long id) {
        deviceHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
