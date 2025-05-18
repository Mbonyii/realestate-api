package property.management.controller;


import property.management.dto.request.*;
import property.management.model.Amenity;
import property.management.service.AmenityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping("/public/amenities")
    public ResponseEntity<List<Amenity>> getAllAmenities() {
        return ResponseEntity.ok(amenityService.getAllAmenities());
    }

    @GetMapping("/public/amenities/{id}")
    public ResponseEntity<Amenity> getAmenityById(@PathVariable Long id) {
        return ResponseEntity.ok(amenityService.getAmenityById(id));
    }

    @GetMapping("/public/amenities/name/{name}")
    public ResponseEntity<Amenity> getAmenityByName(@PathVariable String name) {
        return ResponseEntity.ok(amenityService.getAmenityByName(name));
    }

    @PostMapping("/admin/amenities")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Amenity> createAmenity(@RequestBody Amenity amenity) {
        return ResponseEntity.ok(amenityService.createAmenity(amenity));
    }

    @PutMapping("/admin/amenities/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Amenity> updateAmenity(
            @PathVariable Long id,
            @RequestBody Amenity amenityDetails) {
        return ResponseEntity.ok(amenityService.updateAmenity(id, amenityDetails));
    }

    @DeleteMapping("/admin/amenities/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteAmenity(@PathVariable Long id) {
        return ResponseEntity.ok(amenityService.deleteAmenity(id));
    }
}