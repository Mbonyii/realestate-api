package property.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 50, nullable = false)
    private String firstName;
    
    @Column(length = 50, nullable = false)
    private String lastName;
    
    @Column(length = 255, nullable = false, unique = true)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(length = 120, nullable = false)
    private String password;
    
    @Column(length = 255)
    private String address;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    private boolean enabled = false;
    
    private String twoFactorSecret;
    
    private boolean twoFactorEnabled = false;
    
    private String passwordResetToken;
    
    private LocalDateTime passwordResetTokenExpiry;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "agent")
    private Set<Property> properties = new HashSet<>();
    
    @OneToMany(mappedBy = "user")
    private Set<Rating> ratings = new HashSet<>();
    
    @OneToMany(mappedBy = "agent")
    private Set<Transaction> agentTransactions = new HashSet<>();
    
    @OneToMany(mappedBy = "client")
    private Set<Transaction> clientTransactions = new HashSet<>();
}