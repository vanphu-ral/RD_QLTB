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
import rd.project.qltb.model.LineDTO;
import rd.project.qltb.service.LineService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


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
    public ResponseEntity<LineDTO> getLine(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(lineService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createLine(@RequestBody @Valid final LineDTO lineDTO) {
        final Integer createdId = lineService.create(lineDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateLine(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final LineDTO lineDTO) {
        lineService.update(id, lineDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteLine(@PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = lineService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        lineService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
