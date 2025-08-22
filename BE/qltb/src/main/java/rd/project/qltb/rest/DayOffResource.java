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
import rd.project.qltb.model.DayOffDTO;
import rd.project.qltb.service.DayOffService;


@RestController
@RequestMapping(value = "/api/dayOffs", produces = MediaType.APPLICATION_JSON_VALUE)
public class DayOffResource {

    private final DayOffService dayOffService;

    public DayOffResource(final DayOffService dayOffService) {
        this.dayOffService = dayOffService;
    }

    @GetMapping
    public ResponseEntity<List<DayOffDTO>> getAllDayOffs() {
        return ResponseEntity.ok(dayOffService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DayOffDTO> getDayOff(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(dayOffService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDayOff(@RequestBody @Valid final DayOffDTO dayOffDTO) {
        final Long createdId = dayOffService.create(dayOffDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDayOff(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final DayOffDTO dayOffDTO) {
        dayOffService.update(id, dayOffDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDayOff(@PathVariable(name = "id") final Long id) {
        dayOffService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
