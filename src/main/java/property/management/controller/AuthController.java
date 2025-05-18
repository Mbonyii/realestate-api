package property.management.controller;

import property.management.dto.request.LoginRequest;
import property.management.dto.request.ResetPasswordRequest;
import property.management.dto.request.SignupRequest;
import property.management.dto.request.TwoFactorVerificationRequest;
import property.management.dto.response.JwtResponse;
import property.management.dto.response.MessageResponse;
import property.management.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticate(loginRequest));
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.register(signupRequest));
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<JwtResponse> verifyTwoFactor(@Valid @RequestBody TwoFactorVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyTwoFactor(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @GetMapping("/2fa/generate/{userId}")
    public ResponseEntity<String> generateTwoFactorQrCode(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.generateTwoFactorQrCode(userId));
    }

    @PostMapping("/2fa/enable/{userId}")
    public ResponseEntity<MessageResponse> enableTwoFactor(
            @PathVariable Long userId,
            @RequestParam String code) {
        return ResponseEntity.ok(authService.enableTwoFactor(userId, code));
    }

    @PostMapping("/2fa/disable/{userId}")
    public ResponseEntity<MessageResponse> disableTwoFactor(
            @PathVariable Long userId,
            @RequestParam String code) {
        return ResponseEntity.ok(authService.disableTwoFactor(userId, code));
    }
}