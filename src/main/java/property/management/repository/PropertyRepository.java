package property.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import property.management.model.Property;
import property.management.model.User;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    List<Property> findByAgent(User agent);
    
    List<Property> findByStatus(String status);
    
    List<Property> findByCategoryId(Long categoryId);
    
    List<Property> findByLocationContaining(String location);
    
    Optional<Property> findByTitle(String title);
}