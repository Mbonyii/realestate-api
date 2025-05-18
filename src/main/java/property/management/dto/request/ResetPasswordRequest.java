package property.management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    
    @Email(message = "Email should be valid")
    private String email;
    
    private String token;
    
    @Size(min = 6, max = 120, message = "Password must be between 6 and 120 characters")
    private String newPassword;
}