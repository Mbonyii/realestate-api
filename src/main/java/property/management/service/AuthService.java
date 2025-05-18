package property.management.service;

import property.management.dto.request.LoginRequest;
import property.management.dto.request.ResetPasswordRequest;
import property.management.dto.request.SignupRequest;
import property.management.dto.request.TwoFactorVerificationRequest;
import property.management.dto.response.JwtResponse;
import property.management.dto.response.MessageResponse;
import property.management.exception.BadRequestException;
import property.management.exception.ResourceNotFoundException;
import property.management.model.User;
import property.management.repository.UserRepository;
import property.management.security.JwtTokenProvider;
import property.management.security.TwoFactorAuthenticationService;
import property.management.security.UserDetailsImpl;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final TwoFactorAuthenticationService twoFactorService;
    private final EmailService emailService;

    @Transactional
    public JwtResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        JwtResponse.JwtResponseBuilder responseBuilder = JwtResponse.builder()
                .token(jwt)
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .roles(roles)
                .twoFactorEnabled(userDetails.isTwoFactorEnabled());

        if (userDetails.isTwoFactorEnabled()) {
            responseBuilder.authenticated(false);
        } else {
            responseBuilder.authenticated(true);
        }

        return responseBuilder.build();
    }

    @Transactional
    public MessageResponse register(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new BadRequestException("Email is already taken");
        }

        User user = new User();
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setPhone(signupRequest.getPhone());
        user.setAddress(signupRequest.getAddress());
        user.setRole(signupRequest.getRole());
        user.setEnabled(true);

        if (signupRequest.isEnableTwoFactor()) {
            String secret = twoFactorService.generateNewSecret();
            user.setTwoFactorSecret(secret);
            user.setTwoFactorEnabled(true);
        }

        userRepository.save(user);

        try {
            emailService.sendWelcomeEmail(user);
        } catch (MessagingException e) {
            // Log the error but don't fail the registration
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }

        return MessageResponse.builder()
                .message("User registered successfully")
                .success(true)
                .build();
    }

    @Transactional
    public JwtResponse verifyTwoFactor(TwoFactorVerificationRequest request) {
        String token = request.getToken();
        
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BadRequestException("Invalid token");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (!twoFactorService.isCodeValid(user.getTwoFactorSecret(), request.getCode())) {
            throw new BadRequestException("Invalid authentication code");
        }
        
        // Create a new authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Generate a new token that includes the two-factor verification
        String newToken = jwtTokenProvider.generateToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        return JwtResponse.builder()
                .token(newToken)
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .roles(roles)
                .twoFactorEnabled(true)
                .authenticated(true)
                .build();
    }

    @Transactional
    public MessageResponse forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        
        String token = UUID.randomUUID().toString();
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        
        try {
            emailService.sendPasswordResetEmail(user);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
        
        return MessageResponse.builder()
                .message("Password reset email sent")
                .success(true)
                .build();
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user;
        
        // If email is provided, find by email
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.getEmail()));
        } else if (request.getToken() != null && !request.getToken().isEmpty()) {
            // If token is provided, find by token
            user = userRepository.findByPasswordResetToken(request.getToken())
                    .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired password reset token"));
            
            if (user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
                throw new BadRequestException("Password reset token has expired");
            }
        } else {
            throw new BadRequestException("Email or token must be provided");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiry(null);
        userRepository.save(user);
        
        return MessageResponse.builder()
                .message("Password has been reset successfully")
                .success(true)
                .build();
    }

    public String generateTwoFactorQrCode(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (!user.isTwoFactorEnabled() || user.getTwoFactorSecret() == null) {
            String secret = twoFactorService.generateNewSecret();
            user.setTwoFactorSecret(secret);
            user.setTwoFactorEnabled(true);
            userRepository.save(user);
        }
        
        return twoFactorService.generateQrCodeImageUri(user.getTwoFactorSecret(), user.getEmail());
    }

    @Transactional
    public MessageResponse enableTwoFactor(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (user.isTwoFactorEnabled()) {
            throw new BadRequestException("Two-factor authentication is already enabled");
        }
        
        if (user.getTwoFactorSecret() == null) {
            String secret = twoFactorService.generateNewSecret();
            user.setTwoFactorSecret(secret);
            userRepository.save(user);
        }
        
        if (!twoFactorService.isCodeValid(user.getTwoFactorSecret(), code)) {
            throw new BadRequestException("Invalid authentication code");
        }
        
        user.setTwoFactorEnabled(true);
        userRepository.save(user);
        
        return MessageResponse.builder()
                .message("Two-factor authentication enabled successfully")
                .success(true)
                .build();
    }

    @Transactional
    public MessageResponse disableTwoFactor(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        if (!user.isTwoFactorEnabled()) {
            throw new BadRequestException("Two-factor authentication is already disabled");
        }
        
        if (!twoFactorService.isCodeValid(user.getTwoFactorSecret(), code)) {
            throw new BadRequestException("Invalid authentication code");
        }
        
        user.setTwoFactorEnabled(false);
        user.setTwoFactorSecret(null);
        userRepository.save(user);
        
        return MessageResponse.builder()
                .message("Two-factor authentication disabled successfully")
                .success(true)
                .build();
    }
}