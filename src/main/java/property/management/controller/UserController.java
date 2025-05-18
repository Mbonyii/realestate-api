package property.management.controller;

import property.management.dto.request.*;
import property.management.model.User;
import property.management.service.UserService;
import property.management.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/admin/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @PutMapping("/admin/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> changeRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        return ResponseEntity.ok(userService.changeRole(id, role));
    }

    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @GetMapping("/client/profile")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<User> getProfile() {
        // Get the currently authenticated user from the security context
        // This is a placeholder - in a real implementation, you would get the user ID from the authentication
        Long userId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/client/profile")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<User> updateProfile(@RequestBody User userDetails) {
        // Get the currently authenticated user from the security context
        // This is a placeholder - in a real implementation, you would get the user ID from the authentication
        Long userId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(userService.updateUser(userId, userDetails));
    }

    @PutMapping("/client/change-password")
    @PreAuthorize("hasAnyRole('CLIENT', 'AGENT', 'ADMIN')")
    public ResponseEntity<MessageResponse> changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        // Get the currently authenticated user from the security context
        // This is a placeholder - in a real implementation, you would get the user ID from the authentication
        Long userId = 1L; // Replace with actual implementation
        return ResponseEntity.ok(userService.changePassword(userId, currentPassword, newPassword));
    }
}