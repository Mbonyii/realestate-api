package property.management.controller;

import property.management.dto.request.*;
import property.management.model.Rating;
import property.management.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/public/ratings/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.getRatingById(id));
    }

    @GetMapping("/public/properties/{propertyId}/ratings")
    public ResponseEntity<List<Rating>> getRatingsByPropertyId(@PathVariable Long propertyId) {
        return ResponseEntity.ok(ratingService.getRatingsByPropertyId(propertyId));
    }

    @GetMapping("/admin/users/{userId}/ratings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Rating>> getRatingsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getRatingsByUserId(userId));
    }

    @GetMapping("/client/my-ratings")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<List<Rating>> getMyRatings() {
        // Get the currently authenticated user from the security context
        // This is a placeholder - in a real implementation, you would get the user ID from the authentication
        Long userId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(ratingService.getRatingsByUserId(userId));
    }

    @PostMapping("/client/properties/{propertyId}/ratings")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Rating> createRating(
            @RequestBody Rating rating,
            @PathVariable Long propertyId) {
        // Get the currently authenticated user from the security context
        // This is a placeholder - in a real implementation, you would get the user ID from the authentication
        Long userId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(ratingService.createRating(rating, userId, propertyId));
    }

    @PutMapping("/client/ratings/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<Rating> updateRating(
            @PathVariable Long id,
            @RequestBody Rating ratingDetails) {
        // In a real implementation, you would check if the rating belongs to the authenticated user
        return ResponseEntity.ok(ratingService.updateRating(id, ratingDetails));
    }

    @DeleteMapping("/client/ratings/{id}")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<MessageResponse> deleteRating(@PathVariable Long id) {
        // In a real implementation, you would check if the rating belongs to the authenticated user
        return ResponseEntity.ok(ratingService.deleteRating(id));
    }

    @DeleteMapping("/admin/ratings/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> adminDeleteRating(@PathVariable Long id) {
        return ResponseEntity.ok(ratingService.deleteRating(id));
    }
}