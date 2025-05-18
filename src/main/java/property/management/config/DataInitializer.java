package property.management.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import property.management.model.Role;
import property.management.model.User;
import property.management.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@property.com}")
    private String adminEmail;

    @Value("${app.admin.password:Admin@123}")
    private String adminPassword;

    @Value("${app.admin.firstName:System}")
    private String adminFirstName;

    @Value("${app.admin.lastName:Administrator}")
    private String adminLastName;

    @Value("${app.init-admin:true}")
    private boolean initAdmin;

    @Override
    public void run(String... args) {
        if (initAdmin) {
            createAdminUserIfNotExists();
        }
    }

    private void createAdminUserIfNotExists() {
        if (userRepository.findByEmail(adminEmail).isEmpty()) {
            log.info("Creating admin user: {}", adminEmail);
            
            User adminUser = new User();
            adminUser.setEmail(adminEmail);
            adminUser.setPassword(passwordEncoder.encode(adminPassword));
            adminUser.setFirstName(adminFirstName);
            adminUser.setLastName(adminLastName);
            adminUser.setRole(Role.ADMIN);
            adminUser.setEnabled(true);
            adminUser.setTwoFactorEnabled(false);
            adminUser.setPhone("System Admin");
            adminUser.setAddress("System Address");
            
            userRepository.save(adminUser);
            
            log.info("Admin user created successfully");
        } else {
            log.info("Admin user already exists");
        }
    }
}