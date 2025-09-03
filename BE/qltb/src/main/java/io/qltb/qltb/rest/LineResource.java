package io.qltb.qltb.rest;

import io.qltb.qltb.model.LineDTO;
import io.qltb.qltb.service.LineService;
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
@RequestMapping(value = "/api/lines", produces = MediaType.APPLICATION_JSON_VALUE)
public class LineResource {

    private final LineService lineService;

    public LineResource(final LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping
    public ResponseEntity<List<LineDTO>> getAllLines() {
        return ResponseEntity.ok(lineService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineDTO> getLine(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(lineService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createLine(@RequestBody @Valid final LineDTO lineDTO) {
        final Long createdId = lineService.create(lineDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateLine(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final LineDTO lineDTO) {
        lineService.update(id, lineDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteLine(@PathVariable(name = "id") final Long id) {
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
