package property.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(precision = 38, scale = 2, nullable = false)
    private BigDecimal amount;
    
    @Column(precision = 38, scale = 2)
    private BigDecimal commission;
    
    private LocalDateTime date;
    
    @Column(length = 255)
    private String status;
    
    @Column(length = 255)
    private String transactionType;
    
    @ManyToOne
    @JoinColumn(name = "property_id")
    private Property property;
    
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;
    
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;
}