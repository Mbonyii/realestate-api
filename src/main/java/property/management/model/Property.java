package property.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 255, nullable = false)
    private String title;
    
    @Column(length = 255, nullable = false)
    private String description;
    
    @Column(length = 255, nullable = false)
    private String location;
    
    @Column(precision = 38, scale = 2)
    private BigDecimal price;
    
    @Column(length = 255)
    private String status;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;
    
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private Set<Image> images = new HashSet<>();
    
    @OneToMany(mappedBy = "property")
    private Set<Rating> ratings = new HashSet<>();
    
    @OneToMany(mappedBy = "property")
    private Set<Transaction> transactions = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "property_amenities",
        joinColumns = @JoinColumn(name = "property_id"),
        inverseJoinColumns = @JoinColumn(name = "amenity_id")
    )
    private Set<Amenity> amenities = new HashSet<>();
    
    private Integer score;
}