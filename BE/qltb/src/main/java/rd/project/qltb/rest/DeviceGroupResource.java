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
import rd.project.qltb.model.DeviceGroupDTO;
import rd.project.qltb.service.DeviceGroupService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/deviceGroups", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceGroupResource {

    private final DeviceGroupService deviceGroupService;

    public DeviceGroupResource(final DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceGroupDTO>> getAllDeviceGroups() {
        return ResponseEntity.ok(deviceGroupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceGroupDTO> getDeviceGroup(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(deviceGroupService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createDeviceGroup(
            @RequestBody @Valid final DeviceGroupDTO deviceGroupDTO) {
        final Integer createdId = deviceGroupService.create(deviceGroupDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateDeviceGroup(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final DeviceGroupDTO deviceGroupDTO) {
        deviceGroupService.update(id, deviceGroupDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDeviceGroup(@PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = deviceGroupService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        deviceGroupService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
