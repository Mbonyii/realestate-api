package property.management.service;

import property.management.dto.request.*;
import property.management.exception.ResourceNotFoundException;
import property.management.model.Image;
import property.management.model.Property;
import property.management.repository.ImageRepository;
import property.management.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final PropertyRepository propertyRepository;
    
    private final String uploadDir = "uploads/images/";

    public List<Image> getAllImages() {
        return imageRepository.findAll();
    }

    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id));
    }

    public List<Image> getImagesByPropertyId(Long propertyId) {
        return imageRepository.findByPropertyId(propertyId);
    }

    @Transactional
    public Image uploadImage(MultipartFile file, Long propertyId, String description) throws IOException {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate a unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID().toString() + extension;
        
        // Save the file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        // Create image record
        Image image = new Image();
        image.setUrl(uploadDir + filename);
        image.setDescription(description);
        image.setProperty(property);
        
        return imageRepository.save(image);
    }

    @Transactional
    public Image updateImage(Long id, String description) {
        Image image = getImageById(id);
        image.setDescription(description);
        return imageRepository.save(image);
    }

    @Transactional
    public MessageResponse deleteImage(Long id) throws IOException {
        Image image = getImageById(id);
        
        // Delete file from filesystem
        Path filePath = Paths.get(image.getUrl());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        
        // Delete record from database
        imageRepository.delete(image);
        
        return MessageResponse.builder()
                .message("Image deleted successfully")
                .success(true)
                .build();
    }
}