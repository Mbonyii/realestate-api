package property.management.repository;

import property.management.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    
    List<Rating> findByPropertyId(Long propertyId);
    
    List<Rating> findByUserId(Long userId);
    
    Optional<Rating> findByUserIdAndPropertyId(Long userId, Long propertyId);
}
