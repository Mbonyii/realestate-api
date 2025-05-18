package property.management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorVerificationRequest {
    
    @NotBlank(message = "Authentication code is required")
    @Size(min = 6, max = 6, message = "Authentication code must be exactly 6 digits")
    private String code;
    
    @NotBlank(message = "Token is required")
    private String token;
}
