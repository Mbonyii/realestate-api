package property.management.service;


import property.management.dto.request.*;
import property.management.exception.ResourceNotFoundException;
import property.management.model.Amenity;
import property.management.model.Category;
import property.management.model.Property;
import property.management.model.User;
import property.management.repository.AmenityRepository;
import property.management.repository.CategoryRepository;
import property.management.repository.PropertyRepository;
import property.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AmenityRepository amenityRepository;

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
    }

    public List<Property> getPropertiesByAgentId(Long agentId) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + agentId));
        
        return propertyRepository.findByAgent(agent);
    }

    public List<Property> getPropertiesByStatus(String status) {
        return propertyRepository.findByStatus(status);
    }

    public List<Property> getPropertiesByCategory(Long categoryId) {
        return propertyRepository.findByCategoryId(categoryId);
    }

    public List<Property> getPropertiesByLocation(String location) {
        return propertyRepository.findByLocationContaining(location);
    }

    @Transactional
    public Property createProperty(Property property, Long agentId, Long categoryId, List<Long> amenityIds) {
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + agentId));
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        
        property.setAgent(agent);
        property.setCategory(category);
        
        if (amenityIds != null && !amenityIds.isEmpty()) {
            Set<Amenity> amenities = amenityIds.stream()
                    .map(id -> amenityRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + id)))
                    .collect(Collectors.toSet());
            
            property.setAmenities(amenities);
        }
        
        return propertyRepository.save(property);
    }

    @Transactional
    public Property updateProperty(Long id, Property propertyDetails, Long categoryId, List<Long> amenityIds) {
        Property property = getPropertyById(id);
        
        property.setTitle(propertyDetails.getTitle());
        property.setDescription(propertyDetails.getDescription());
        property.setLocation(propertyDetails.getLocation());
        property.setPrice(propertyDetails.getPrice());
        property.setStatus(propertyDetails.getStatus());
        property.setScore(propertyDetails.getScore());
        
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
            property.setCategory(category);
        }
        
        if (amenityIds != null && !amenityIds.isEmpty()) {
            Set<Amenity> amenities = amenityIds.stream()
                    .map(amenityId -> amenityRepository.findById(amenityId)
                            .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + amenityId)))
                    .collect(Collectors.toSet());
            
            property.setAmenities(amenities);
        }
        
        return propertyRepository.save(property);
    }

    @Transactional
    public MessageResponse deleteProperty(Long id) {
        Property property = getPropertyById(id);
        propertyRepository.delete(property);
        
        return MessageResponse.builder()
                .message("Property deleted successfully")
                .success(true)
                .build();
    }

    @Transactional
    public Property updatePropertyStatus(Long id, String status) {
        Property property = getPropertyById(id);
        property.setStatus(status);
        return propertyRepository.save(property);
    }

    @Transactional
    public Property addAmenityToProperty(Long propertyId, Long amenityId) {
        Property property = getPropertyById(propertyId);
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + amenityId));
        
        property.getAmenities().add(amenity);
        return propertyRepository.save(property);
    }

    @Transactional
    public Property removeAmenityFromProperty(Long propertyId, Long amenityId) {
        Property property = getPropertyById(propertyId);
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + amenityId));
        
        property.getAmenities().remove(amenity);
        return propertyRepository.save(property);
    }
}
