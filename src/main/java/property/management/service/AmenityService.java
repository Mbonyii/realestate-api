package property.management.service;

import property.management.dto.request.*;
import property.management.exception.ResourceNotFoundException;
import property.management.model.Amenity;
import property.management.repository.AmenityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public List<Amenity> getAllAmenities() {
        return amenityRepository.findAll();
    }

    public Amenity getAmenityById(Long id) {
        return amenityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with id: " + id));
    }

    public Amenity getAmenityByName(String name) {
        return amenityRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity not found with name: " + name));
    }

    @Transactional
    public Amenity createAmenity(Amenity amenity) {
        if (amenityRepository.existsByName(amenity.getName())) {
            throw new IllegalArgumentException("Amenity name already exists");
        }
        return amenityRepository.save(amenity);
    }

    @Transactional
    public Amenity updateAmenity(Long id, Amenity amenityDetails) {
        Amenity amenity = getAmenityById(id);
        
        // Check if the new name already exists (and it's not the current amenity)
        if (!amenity.getName().equals(amenityDetails.getName()) && 
            amenityRepository.existsByName(amenityDetails.getName())) {
            throw new IllegalArgumentException("Amenity name already exists");
        }
        
        amenity.setName(amenityDetails.getName());
        amenity.setDescription(amenityDetails.getDescription());
        
        return amenityRepository.save(amenity);
    }

    @Transactional
    public MessageResponse deleteAmenity(Long id) {
        Amenity amenity = getAmenityById(id);
        
        // Check if there are properties associated with this amenity
        if (!amenity.getProperties().isEmpty()) {
            throw new IllegalStateException("Cannot delete amenity with associated properties");
        }
        
        amenityRepository.delete(amenity);
        
        return MessageResponse.builder()
                .message("Amenity deleted successfully")
                .success(true)
                .build();
    }
}
