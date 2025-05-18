package property.management.controller;


import property.management.dto.request.*;
import property.management.model.Image;
import property.management.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @GetMapping("/public/images/{id}")
    public ResponseEntity<Image> getImageById(@PathVariable Long id) {
        return ResponseEntity.ok(imageService.getImageById(id));
    }

    @GetMapping("/public/properties/{propertyId}/images")
    public ResponseEntity<List<Image>> getImagesByPropertyId(@PathVariable Long propertyId) {
        return ResponseEntity.ok(imageService.getImagesByPropertyId(propertyId));
    }

    @PostMapping(value = "/agent/properties/{propertyId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Image> uploadImage(
            @RequestParam("file") MultipartFile file,
            @PathVariable Long propertyId,
            @RequestParam(required = false) String description) throws IOException {
        return ResponseEntity.ok(imageService.uploadImage(file, propertyId, description));
    }

    @PutMapping("/agent/images/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Image> updateImageDescription(
            @PathVariable Long id,
            @RequestParam String description) {
        return ResponseEntity.ok(imageService.updateImage(id, description));
    }

    @DeleteMapping("/agent/images/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<MessageResponse> deleteImage(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(imageService.deleteImage(id));
    }
}