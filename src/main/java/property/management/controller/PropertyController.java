package property.management.controller;

import property.management.dto.request.*;
import property.management.model.Property;
import property.management.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping("/public/properties")
    public ResponseEntity<List<Property>> getAllProperties() {
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    @GetMapping("/public/properties/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @GetMapping("/public/properties/status/{status}")
    public ResponseEntity<List<Property>> getPropertiesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(propertyService.getPropertiesByStatus(status));
    }

    @GetMapping("/public/properties/category/{categoryId}")
    public ResponseEntity<List<Property>> getPropertiesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(propertyService.getPropertiesByCategory(categoryId));
    }

    @GetMapping("/public/properties/location")
    public ResponseEntity<List<Property>> getPropertiesByLocation(@RequestParam String location) {
        return ResponseEntity.ok(propertyService.getPropertiesByLocation(location));
    }

    @GetMapping("/agent/properties")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<List<Property>> getAgentProperties() {
        // Get the currently authenticated agent's ID from the security context
        // This is a placeholder - in a real implementation, you would get the agent ID from the authentication
        Long agentId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(propertyService.getPropertiesByAgentId(agentId));
    }

    @PostMapping("/agent/properties")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Property> createProperty(
            @RequestBody Property property,
            @RequestParam Long categoryId,
            @RequestParam(required = false) List<Long> amenityIds) {
        // Get the currently authenticated agent's ID from the security context
        // This is a placeholder - in a real implementation, you would get the agent ID from the authentication
        Long agentId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(propertyService.createProperty(property, agentId, categoryId, amenityIds));
    }

    @PutMapping("/agent/properties/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Property> updateProperty(
            @PathVariable Long id,
            @RequestBody Property propertyDetails,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) List<Long> amenityIds) {
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDetails, categoryId, amenityIds));
    }

    @DeleteMapping("/agent/properties/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<MessageResponse> deleteProperty(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.deleteProperty(id));
    }

    @PatchMapping("/agent/properties/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Property> updatePropertyStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(propertyService.updatePropertyStatus(id, status));
    }

    @PostMapping("/agent/properties/{propertyId}/amenities/{amenityId}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Property> addAmenityToProperty(
            @PathVariable Long propertyId,
            @PathVariable Long amenityId) {
        return ResponseEntity.ok(propertyService.addAmenityToProperty(propertyId, amenityId));
    }

    @DeleteMapping("/agent/properties/{propertyId}/amenities/{amenityId}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Property> removeAmenityFromProperty(
            @PathVariable Long propertyId,
            @PathVariable Long amenityId) {
        return ResponseEntity.ok(propertyService.removeAmenityFromProperty(propertyId, amenityId));
    }
}