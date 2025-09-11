package io.rd.qltb.rest;

import io.rd.qltb.model.AssetHistoryDTO;
import io.rd.qltb.service.AssetHistoryService;
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
@RequestMapping(value = "/api/assetHistories", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssetHistoryResource {

    private final AssetHistoryService assetHistoryService;

    public AssetHistoryResource(final AssetHistoryService assetHistoryService) {
        this.assetHistoryService = assetHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<AssetHistoryDTO>> getAllAssetHistories() {
        return ResponseEntity.ok(assetHistoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetHistoryDTO> getAssetHistory(
            @PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(assetHistoryService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createAssetHistory(
            @RequestBody @Valid final AssetHistoryDTO assetHistoryDTO) {
        final Long createdId = assetHistoryService.create(assetHistoryDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateAssetHistory(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final AssetHistoryDTO assetHistoryDTO) {
        assetHistoryService.update(id, assetHistoryDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteAssetHistory(@PathVariable(name = "id") final Long id) {
        assetHistoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
