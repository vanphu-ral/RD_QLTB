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
import rd.project.qltb.model.FormDTO;
import rd.project.qltb.service.FormService;


@RestController
@RequestMapping(value = "/api/forms", produces = MediaType.APPLICATION_JSON_VALUE)
public class FormResource {

    private final FormService formService;

    public FormResource(final FormService formService) {
        this.formService = formService;
    }

    @GetMapping
    public ResponseEntity<List<FormDTO>> getAllForms() {
        return ResponseEntity.ok(formService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormDTO> getForm(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(formService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createForm(@RequestBody @Valid final FormDTO formDTO) {
        final Long createdId = formService.create(formDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateForm(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final FormDTO formDTO) {
        formService.update(id, formDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteForm(@PathVariable(name = "id") final Long id) {
        formService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
