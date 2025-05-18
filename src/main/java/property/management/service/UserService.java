package property.management.service;
import property.management.dto.request.*;
import property.management.exception.ResourceNotFoundException;
import property.management.model.User;
import property.management.model.Role;
import property.management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Transactional
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setPhone(userDetails.getPhone());
        user.setAddress(userDetails.getAddress());
        
        return userRepository.save(user);
    }

    @Transactional
    public MessageResponse changePassword(Long id, String currentPassword, String newPassword) {
        User user = getUserById(id);
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        return MessageResponse.builder()
                .message("Password changed successfully")
                .success(true)
                .build();
    }

    @Transactional
    public MessageResponse changeRole(Long id, Role newRole) {
        User user = getUserById(id);
        user.setRole(newRole);
        userRepository.save(user);
        
        return MessageResponse.builder()
                .message("User role updated to: " + newRole)
                .success(true)
                .build();
    }

    @Transactional
    public MessageResponse deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        
        return MessageResponse.builder()
                .message("User deleted successfully")
                .success(true)
                .build();
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }
}