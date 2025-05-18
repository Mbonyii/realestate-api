package property.management.service;

import property.management.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendWelcomeEmail(User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context context = new Context();
        context.setVariable("name", user.getFirstName() + " " + user.getLastName());
        context.setVariable("loginLink", baseUrl + "/login");
        
        String htmlContent = templateEngine.process("email/welcome", context);
        
        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Welcome to Property Management System");
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }

    public void sendPasswordResetEmail(User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context context = new Context();
        context.setVariable("name", user.getFirstName());
        context.setVariable("resetLink", baseUrl + "/reset-password?token=" + user.getPasswordResetToken());
        context.setVariable("expiryTime", "1 hour");
        
        String htmlContent = templateEngine.process("email/reset-password", context);
        
        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Reset Your Password");
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }

    public void sendTwoFactorSetupEmail(User user, String qrCodeImageUri) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context context = new Context();
        context.setVariable("name", user.getFirstName());
        context.setVariable("qrCodeImage", qrCodeImageUri);
        
        String htmlContent = templateEngine.process("email/two-factor-setup", context);
        
        helper.setFrom(fromEmail);
        helper.setTo(user.getEmail());
        helper.setSubject("Setup Two-Factor Authentication");
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }
}