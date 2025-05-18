package property.management.config;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import property.management.model.Amenity;
import property.management.model.Category;
import property.management.model.Property;
import property.management.model.Rating;
import property.management.model.Role;
import property.management.model.Transaction;
import property.management.model.User;
import property.management.repository.AmenityRepository;
import property.management.repository.CategoryRepository;
import property.management.repository.PropertyRepository;
import property.management.repository.RatingRepository;
import property.management.repository.TransactionRepository;
import property.management.repository.UserRepository;

/**
 * This class seeds the database with test data.
 * It will only run in the "dev" or "test" profiles to avoid seeding production data.
 * To use it, run the application with "--spring.profiles.active=dev" or set the profile in application.properties.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile({"dev", "test"})
public class TestDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyRepository propertyRepository;
    private final TransactionRepository transactionRepository;
    private final RatingRepository ratingRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.test-data.enabled:false}")
    private boolean testDataEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (testDataEnabled) {
            log.info("Seeding test data...");
            
            // Check if data already exists
            if (userRepository.count() > 1) {
                log.info("Test data already exists, skipping seeding");
                return;
            }
            
            createUsers();
            createCategories();
            createAmenities();
            createProperties();
            createTransactions();
            createRatings();
            
            log.info("Test data seeding completed");
        }
    }

    private void createUsers() {
        log.info("Creating test users");
        
        // Create agent user
        User agent = new User();
        agent.setEmail("agent@property.com");
        agent.setPassword(passwordEncoder.encode("Agent@123"));
        agent.setFirstName("John");
        agent.setLastName("Agent");
        agent.setRole(Role.AGENT);
        agent.setEnabled(true);
        agent.setTwoFactorEnabled(false);
        agent.setPhone("123-456-7890");
        agent.setAddress("123 Agent Street, City");
        userRepository.save(agent);
        
        // Create client user
        User client = new User();
        client.setEmail("client@property.com");
        client.setPassword(passwordEncoder.encode("Client@123"));
        client.setFirstName("Jane");
        client.setLastName("Client");
        client.setRole(Role.CLIENT);
        client.setEnabled(true);
        client.setTwoFactorEnabled(false);
        client.setPhone("987-654-3210");
        client.setAddress("456 Client Avenue, City");
        userRepository.save(client);
    }

    private void createCategories() {
        log.info("Creating property categories");
        
        String[][] categoryData = {
            {"Apartment", "Residential units in multi-story buildings"},
            {"House", "Standalone residential buildings"},
            {"Commercial", "Properties for business purposes"},
            {"Land", "Undeveloped land parcels"},
            {"Villa", "Luxury standalone houses"}
        };
        
        for (String[] data : categoryData) {
            Category category = new Category();
            category.setName(data[0]);
            category.setDescription(data[1]);
            categoryRepository.save(category);
        }
    }

    private void createAmenities() {
        log.info("Creating property amenities");
        
        String[][] amenityData = {
            {"Swimming Pool", "Outdoor swimming pool"},
            {"Gym", "Fitness center with equipment"},
            {"Parking", "Covered parking spaces"},
            {"Security", "24/7 security service"},
            {"Wi-Fi", "High-speed internet access"},
            {"Air Conditioning", "Central air conditioning system"},
            {"Garden", "Landscaped garden area"},
            {"Elevator", "Building elevator service"}
        };
        
        for (String[] data : amenityData) {
            Amenity amenity = new Amenity();
            amenity.setName(data[0]);
            amenity.setDescription(data[1]);
            amenityRepository.save(amenity);
        }
    }

    private void createProperties() {
        log.info("Creating properties");
        
        User agent = userRepository.findByEmail("agent@property.com").orElseThrow();
        
        // Get categories
        Category apartment = categoryRepository.findByName("Apartment").orElseThrow();
        Category house = categoryRepository.findByName("House").orElseThrow();
        Category commercial = categoryRepository.findByName("Commercial").orElseThrow();
        
        // Get amenities
        Amenity pool = amenityRepository.findByName("Swimming Pool").orElseThrow();
        Amenity gym = amenityRepository.findByName("Gym").orElseThrow();
        Amenity parking = amenityRepository.findByName("Parking").orElseThrow();
        Amenity security = amenityRepository.findByName("Security").orElseThrow();
        Amenity wifi = amenityRepository.findByName("Wi-Fi").orElseThrow();
        Amenity ac = amenityRepository.findByName("Air Conditioning").orElseThrow();
        
        // Create apartment property
        Property apartment1 = new Property();
        apartment1.setTitle("Modern City Apartment");
        apartment1.setDescription("A beautiful modern apartment in the heart of the city with amazing views.");
        apartment1.setLocation("Downtown, City Center");
        apartment1.setPrice(new BigDecimal("250000.00"));
        apartment1.setStatus("AVAILABLE");
        apartment1.setAgent(agent);
        apartment1.setCategory(apartment);
        
        Set<Amenity> apartmentAmenities = new HashSet<>();
        apartmentAmenities.add(gym);
        apartmentAmenities.add(parking);
        apartmentAmenities.add(security);
        apartmentAmenities.add(wifi);
        apartmentAmenities.add(ac);
        apartment1.setAmenities(apartmentAmenities);
        
        propertyRepository.save(apartment1);
        
        // Create house property
        Property house1 = new Property();
        house1.setTitle("Suburban Family House");
        house1.setDescription("A spacious family house in a quiet suburban neighborhood with a large garden.");
        house1.setLocation("Greenfield Suburb");
        house1.setPrice(new BigDecimal("450000.00"));
        house1.setStatus("AVAILABLE");
        house1.setAgent(agent);
        house1.setCategory(house);
        
        Set<Amenity> houseAmenities = new HashSet<>();
        houseAmenities.add(pool);
        houseAmenities.add(parking);
        houseAmenities.add(security);
        houseAmenities.add(wifi);
        houseAmenities.add(ac);
        house1.setAmenities(houseAmenities);
        
        propertyRepository.save(house1);
        
        // Create commercial property
        Property commercial1 = new Property();
        commercial1.setTitle("Office Space in Business District");
        commercial1.setDescription("A modern office space in the business district, perfect for startups and small businesses.");
        commercial1.setLocation("Business District");
        commercial1.setPrice(new BigDecimal("350000.00"));
        commercial1.setStatus("AVAILABLE");
        commercial1.setAgent(agent);
        commercial1.setCategory(commercial);
        
        Set<Amenity> commercialAmenities = new HashSet<>();
        commercialAmenities.add(parking);
        commercialAmenities.add(security);
        commercialAmenities.add(wifi);
        commercialAmenities.add(ac);
        commercial1.setAmenities(commercialAmenities);
        
        propertyRepository.save(commercial1);
    }

    private void createTransactions() {
        log.info("Creating transactions");
        
        User agent = userRepository.findByEmail("agent@property.com").orElseThrow();
        User client = userRepository.findByEmail("client@property.com").orElseThrow();
        
        // Get a property (the house) to mark as sold
        Property house = propertyRepository.findByTitle("Suburban Family House").orElseThrow();
        
        // Create a completed sale transaction
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("435000.00"));  // Negotiated price
        transaction.setCommission(new BigDecimal("13050.00"));  // 3% commission
        transaction.setDate(LocalDateTime.now().minusDays(5));
        transaction.setStatus("COMPLETED");
        transaction.setTransactionType("SALE");
        transaction.setProperty(house);
        transaction.setAgent(agent);
        transaction.setClient(client);
        
        transactionRepository.save(transaction);
        
        // Update property status
        house.setStatus("SOLD");
        propertyRepository.save(house);
        
        // Create a pending transaction for the apartment
        Property apartment = propertyRepository.findByTitle("Modern City Apartment").orElseThrow();
        
        Transaction pendingTransaction = new Transaction();
        pendingTransaction.setAmount(new BigDecimal("245000.00"));  // Negotiated price
        pendingTransaction.setCommission(new BigDecimal("7350.00"));  // 3% commission
        pendingTransaction.setDate(LocalDateTime.now().minusDays(1));
        pendingTransaction.setStatus("PENDING");
        pendingTransaction.setTransactionType("SALE");
        pendingTransaction.setProperty(apartment);
        pendingTransaction.setAgent(agent);
        pendingTransaction.setClient(client);
        
        transactionRepository.save(pendingTransaction);
    }

    private void createRatings() {
        log.info("Creating ratings");
        
        User client = userRepository.findByEmail("client@property.com").orElseThrow();
        
        // Get properties
        Property house = propertyRepository.findByTitle("Suburban Family House").orElseThrow();
        Property apartment = propertyRepository.findByTitle("Modern City Apartment").orElseThrow();
        
        // Create rating for the house
        Rating houseRating = new Rating();
        houseRating.setScore(5);
        houseRating.setComment("Excellent property! The house is spacious and the neighborhood is quiet and friendly. The garden is perfect for family barbecues.");
        houseRating.setUser(client);
        houseRating.setProperty(house);
        
        ratingRepository.save(houseRating);
        
        // Create rating for the apartment
        Rating apartmentRating = new Rating();
        apartmentRating.setScore(4);
        apartmentRating.setComment("Great apartment with amazing city views. The location is perfect, close to shops and restaurants. The only downside is the limited parking space.");
        apartmentRating.setUser(client);
        apartmentRating.setProperty(apartment);
        
        ratingRepository.save(apartmentRating);
        
        // Update property scores based on ratings
        house.setScore(5);
        apartment.setScore(4);
        
        propertyRepository.save(house);
        propertyRepository.save(apartment);
    }
}
