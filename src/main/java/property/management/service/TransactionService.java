package property.management.service;
import property.management.dto.request.*;
import property.management.exception.ResourceNotFoundException;
import property.management.model.Property;
import property.management.model.Transaction;
import property.management.model.User;
import property.management.repository.PropertyRepository;
import property.management.repository.TransactionRepository;
import property.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    
    // Default commission rate (can be configurable)
    private final BigDecimal DEFAULT_COMMISSION_RATE = new BigDecimal("0.03"); // 3%

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    public List<Transaction> getTransactionsByPropertyId(Long propertyId) {
        return transactionRepository.findByPropertyId(propertyId);
    }

    public List<Transaction> getTransactionsByAgentId(Long agentId) {
        return transactionRepository.findByAgentId(agentId);
    }

    public List<Transaction> getTransactionsByClientId(Long clientId) {
        return transactionRepository.findByClientId(clientId);
    }

    public List<Transaction> getTransactionsByStatus(String status) {
        return transactionRepository.findByStatus(status);
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction, Long propertyId, Long agentId, Long clientId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        
        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + agentId));
        
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));
        
        // Set relationship entities
        transaction.setProperty(property);
        transaction.setAgent(agent);
        transaction.setClient(client);
        
        // Set date if not provided
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }
        
        // Calculate commission if not provided
        if (transaction.getCommission() == null && transaction.getAmount() != null) {
            BigDecimal commission = transaction.getAmount().multiply(DEFAULT_COMMISSION_RATE)
                    .setScale(2, RoundingMode.HALF_UP);
            transaction.setCommission(commission);
        }
        
        // Update property status if transaction type is sale or rent
        if (("SALE".equalsIgnoreCase(transaction.getTransactionType()) || 
             "RENT".equalsIgnoreCase(transaction.getTransactionType())) && 
            "COMPLETED".equalsIgnoreCase(transaction.getStatus())) {
            
            String newStatus = "SALE".equalsIgnoreCase(transaction.getTransactionType()) ? "SOLD" : "RENTED";
            property.setStatus(newStatus);
            propertyRepository.save(property);
        }
        
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = getTransactionById(id);
        
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setCommission(transactionDetails.getCommission());
        transaction.setDate(transactionDetails.getDate());
        transaction.setStatus(transactionDetails.getStatus());
        transaction.setTransactionType(transactionDetails.getTransactionType());
        
        // If status is changed to COMPLETED, update property status
        if ("COMPLETED".equalsIgnoreCase(transaction.getStatus()) && 
            !transaction.getStatus().equals(transactionDetails.getStatus())) {
            
            Property property = transaction.getProperty();
            
            if ("SALE".equalsIgnoreCase(transaction.getTransactionType())) {
                property.setStatus("SOLD");
            } else if ("RENT".equalsIgnoreCase(transaction.getTransactionType())) {
                property.setStatus("RENTED");
            }
            
            propertyRepository.save(property);
        }
        
        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransactionStatus(Long id, String status) {
        Transaction transaction = getTransactionById(id);
        transaction.setStatus(status);
        
        // If status is changed to COMPLETED, update property status
        if ("COMPLETED".equalsIgnoreCase(status)) {
            Property property = transaction.getProperty();
            
            if ("SALE".equalsIgnoreCase(transaction.getTransactionType())) {
                property.setStatus("SOLD");
            } else if ("RENT".equalsIgnoreCase(transaction.getTransactionType())) {
                property.setStatus("RENTED");
            }
            
            propertyRepository.save(property);
        }
        
        return transactionRepository.save(transaction);
    }

    @Transactional
    public MessageResponse deleteTransaction(Long id) {
        Transaction transaction = getTransactionById(id);
        transactionRepository.delete(transaction);
        
        return MessageResponse.builder()
                .message("Transaction deleted successfully")
                .success(true)
                .build();
    }
}