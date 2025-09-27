package io.rd.qltb.rest;


import io.rd.qltb.domain.UserImage;
import io.rd.qltb.model.UserImageDTO;
import io.rd.qltb.service.UserImageService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "/api/userImages", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserImageResource {

    private final UserImageService userImageService;

    public UserImageResource(final UserImageService userImageService) {
        this.userImageService = userImageService;
    }

    @GetMapping
    public ResponseEntity<List<UserImageDTO>> getAllUserImages() {
        return ResponseEntity.ok(userImageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserImageDTO> getUserImage(@PathVariable(name = "id") final Long id) {
        return ResponseEntity.ok(userImageService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Long> createUserImage(
            @RequestBody @Valid final UserImageDTO userImageDTO) {
        final Long createdId = userImageService.create(userImageDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateUserImage(@PathVariable(name = "id") final Long id,
            @RequestBody @Valid final UserImageDTO userImageDTO) {
        userImageService.update(id, userImageDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteUserImage(@PathVariable(name = "id") final Long id) {
        userImageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserImageDTO> getByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userImageService.getByUsername(username));
    }
}
