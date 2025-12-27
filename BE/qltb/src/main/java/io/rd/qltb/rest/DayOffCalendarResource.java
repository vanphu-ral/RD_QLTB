package io.rd.qltb.rest;

import io.rd.qltb.domain.DayOffCalendar;
import io.rd.qltb.model.DayOffCalendarDTO;
import io.rd.qltb.service.DayOffCalendarService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/dayOffCalendars", produces = MediaType.APPLICATION_JSON_VALUE)
public class DayOffCalendarResource {

    private final DayOffCalendarService dayOffCalendarService;

    public DayOffCalendarResource(final DayOffCalendarService dayOffCalendarService){
        this.dayOffCalendarService = dayOffCalendarService;
    }

    @GetMapping
    public ResponseEntity<List<DayOffCalendarDTO>> getAllDayOffCalendar() {
        return ResponseEntity.ok(dayOffCalendarService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DayOffCalendarDTO> getDayOffCalendar(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(dayOffCalendarService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createDayOffCalendar(@RequestBody @Valid final DayOffCalendarDTO dayOffCalendarDTO) {
        final Long createdId = dayOffCalendarService.create(dayOffCalendarDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateDayOffCalendar(@PathVariable(name = "id") final Long id,
                                             @RequestBody @Valid final DayOffCalendarDTO dayOffCalendarDTO) {
        dayOffCalendarService.update(id, dayOffCalendarDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDayOffCalendar(@PathVariable(name = "id") final Long id) {
        dayOffCalendarService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-team/{teamId}")
    public ResponseEntity<List<DayOffCalendar>> getByTeam(@PathVariable Long teamId) {
        List<DayOffCalendar> list = dayOffCalendarService.getDayOffByTeam(teamId);
        return ResponseEntity.ok(list);
    }
}
