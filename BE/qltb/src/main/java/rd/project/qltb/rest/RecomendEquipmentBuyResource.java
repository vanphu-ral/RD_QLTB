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
import rd.project.qltb.model.RecomendEquipmentBuyDTO;
import rd.project.qltb.service.RecomendEquipmentBuyService;


@RestController
@RequestMapping(value = "/api/recomendEquipmentBuys", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecomendEquipmentBuyResource {

    private final RecomendEquipmentBuyService recomendEquipmentBuyService;

    public RecomendEquipmentBuyResource(
            final RecomendEquipmentBuyService recomendEquipmentBuyService) {
        this.recomendEquipmentBuyService = recomendEquipmentBuyService;
    }

    @GetMapping
    public ResponseEntity<List<RecomendEquipmentBuyDTO>> getAllRecomendEquipmentBuys() {
        return ResponseEntity.ok(recomendEquipmentBuyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecomendEquipmentBuyDTO> getRecomendEquipmentBuy(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(recomendEquipmentBuyService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createRecomendEquipmentBuy(
            @RequestBody @Valid final RecomendEquipmentBuyDTO recomendEquipmentBuyDTO) {
        final Long createdId = recomendEquipmentBuyService.create(recomendEquipmentBuyDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateRecomendEquipmentBuy(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final RecomendEquipmentBuyDTO recomendEquipmentBuyDTO) {
        recomendEquipmentBuyService.update(id, recomendEquipmentBuyDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteRecomendEquipmentBuy(
            @PathVariable(name = "id") final Long id) {
        recomendEquipmentBuyService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
