package property.management.repository;

import property.management.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByPropertyId(Long propertyId);
    
    List<Transaction> findByAgentId(Long agentId);
    
    List<Transaction> findByClientId(Long clientId);
    
    List<Transaction> findByStatus(String status);
    
    List<Transaction> findByTransactionType(String transactionType);
}
