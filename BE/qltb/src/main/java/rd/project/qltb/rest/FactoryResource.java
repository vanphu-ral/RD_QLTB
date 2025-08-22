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
import rd.project.qltb.model.FactoryDTO;
import rd.project.qltb.service.FactoryService;
import rd.project.qltb.util.ReferencedException;
import rd.project.qltb.util.ReferencedWarning;


@RestController
@RequestMapping(value = "/api/factories", produces = MediaType.APPLICATION_JSON_VALUE)
public class FactoryResource {

    private final FactoryService factoryService;

    public FactoryResource(final FactoryService factoryService) {
        this.factoryService = factoryService;
    }

    @GetMapping
    public ResponseEntity<List<FactoryDTO>> getAllFactories() {
        return ResponseEntity.ok(factoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactoryDTO> getFactory(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(factoryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createFactory(@RequestBody @Valid final FactoryDTO factoryDTO) {
        final Integer createdId = factoryService.create(factoryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateFactory(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final FactoryDTO factoryDTO) {
        factoryService.update(id, factoryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteFactory(@PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = factoryService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        factoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
