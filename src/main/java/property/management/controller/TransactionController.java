package property.management.controller;

import property.management.dto.request.*;
import property.management.model.Transaction;
import property.management.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/admin/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @GetMapping("/admin/transactions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/admin/properties/{propertyId}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByPropertyId(@PathVariable Long propertyId) {
        return ResponseEntity.ok(transactionService.getTransactionsByPropertyId(propertyId));
    }

    @GetMapping("/admin/agents/{agentId}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByAgentId(@PathVariable Long agentId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAgentId(agentId));
    }

    @GetMapping("/admin/clients/{clientId}/transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByClientId(@PathVariable Long clientId) {
        return ResponseEntity.ok(transactionService.getTransactionsByClientId(clientId));
    }

    @GetMapping("/admin/transactions/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Transaction>> getTransactionsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(transactionService.getTransactionsByStatus(status));
    }

    @GetMapping("/agent/my-transactions")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getAgentTransactions() {
        // Get the currently authenticated agent's ID from the security context
        // This is a placeholder - in a real implementation, you would get the agent ID from the authentication
        Long agentId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(transactionService.getTransactionsByAgentId(agentId));
    }

    @GetMapping("/client/my-transactions")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN')")
    public ResponseEntity<List<Transaction>> getClientTransactions() {
        // Get the currently authenticated client's ID from the security context
        // This is a placeholder - in a real implementation, you would get the client ID from the authentication
        Long clientId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(transactionService.getTransactionsByClientId(clientId));
    }

    @PostMapping("/agent/transactions")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody Transaction transaction,
            @RequestParam Long propertyId,
            @RequestParam Long clientId) {
        // Get the currently authenticated agent's ID from the security context
        // This is a placeholder - in a real implementation, you would get the agent ID from the authentication
        Long agentId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(transactionService.createTransaction(transaction, propertyId, agentId, clientId));
    }

    @PutMapping("/agent/transactions/{id}")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @RequestBody Transaction transactionDetails) {
        // In a real implementation, you would check if the transaction belongs to the authenticated agent
        return ResponseEntity.ok(transactionService.updateTransaction(id, transactionDetails));
    }

    @PatchMapping("/agent/transactions/{id}/status")
    @PreAuthorize("hasAnyRole('AGENT', 'ADMIN')")
    public ResponseEntity<Transaction> updateTransactionStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        // In a real implementation, you would check if the transaction belongs to the authenticated agent
        return ResponseEntity.ok(transactionService.updateTransactionStatus(id, status));
    }

    @DeleteMapping("/admin/transactions/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteTransaction(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.deleteTransaction(id));
    }
}
