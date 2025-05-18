package property.management.service;

import property.management.dto.request.*;
import property.management.exception.ResourceNotFoundException;
import property.management.model.Property;
import property.management.model.Rating;
import property.management.model.User;
import property.management.repository.PropertyRepository;
import property.management.repository.RatingRepository;
import property.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.OptionalDouble;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Rating getRatingById(Long id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + id));
    }

    public List<Rating> getRatingsByPropertyId(Long propertyId) {
        return ratingRepository.findByPropertyId(propertyId);
    }

    public List<Rating> getRatingsByUserId(Long userId) {
        return ratingRepository.findByUserId(userId);
    }

    public Rating getRatingByUserAndPropertyId(Long userId, Long propertyId) {
        return ratingRepository.findByUserIdAndPropertyId(userId, propertyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rating not found for user id: " + userId + " and property id: " + propertyId));
    }

    @Transactional
    public Rating createRating(Rating rating, Long userId, Long propertyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        
        // Check if user has already rated this property
        if (ratingRepository.findByUserIdAndPropertyId(userId, propertyId).isPresent()) {
            throw new IllegalStateException("User has already rated this property");
        }
        
        rating.setUser(user);
        rating.setProperty(property);
        
        Rating savedRating = ratingRepository.save(rating);
        
        // Update property score
        updatePropertyScore(property);
        
        return savedRating;
    }

    @Transactional
    public Rating updateRating(Long id, Rating ratingDetails) {
        Rating rating = getRatingById(id);
        
        rating.setScore(ratingDetails.getScore());
        rating.setComment(ratingDetails.getComment());
        
        Rating updatedRating = ratingRepository.save(rating);
        
        // Update property score
        updatePropertyScore(rating.getProperty());
        
        return updatedRating;
    }

    @Transactional
    public MessageResponse deleteRating(Long id) {
        Rating rating = getRatingById(id);
        Property property = rating.getProperty();
        
        ratingRepository.delete(rating);
        
        // Update property score
        updatePropertyScore(property);
        
        return MessageResponse.builder()
                .message("Rating deleted successfully")
                .success(true)
                .build();
    }
    
    private void updatePropertyScore(Property property) {
        List<Rating> ratings = ratingRepository.findByPropertyId(property.getId());
        
        if (ratings.isEmpty()) {
            property.setScore(null);
        } else {
            OptionalDouble average = ratings.stream()
                    .mapToInt(Rating::getScore)
                    .average();
            
            if (average.isPresent()) {
                // Round to the nearest integer
                property.setScore((int) Math.round(average.getAsDouble()));
            }
        }
        
        propertyRepository.save(property);
    }
}