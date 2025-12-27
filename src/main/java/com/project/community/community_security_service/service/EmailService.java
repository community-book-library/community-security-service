package com.project.community.community_security_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Send OTP email
     */
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Your OTP Code");
            message.setText(buildOtpEmailContent(otp));

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    /**
     * Send HTML OTP email (better formatting)
     */
    public void sendHtmlOtpEmail(String toEmail, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Your OTP Code");
        helper.setText(buildHtmlOtpEmailContent(otp), true);

        mailSender.send(message);
    }

    private String buildOtpEmailContent(String otp) {
        return String.format(
                "Your OTP code is: %s\n\n" +
                        "This code will expire in 5 minutes.\n" +
                        "Please do not share this code with anyone.\n\n" +
                        "If you didn't request this code, please ignore this email.",
                otp
        );
    }

    private String buildHtmlOtpEmailContent(String otp) {
        return String.format(
                "<html>" +
                        "<body style='font-family: Arial, sans-serif;'>" +
                        "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                        "<h2>Your OTP Code</h2>" +
                        "<div style='background-color: #f5f5f5; padding: 20px; border-radius: 5px; text-align: center;'>" +
                        "<h1 style='color: #4CAF50; font-size: 36px; margin: 0;'>%s</h1>" +
                        "</div>" +
                        "<p style='margin-top: 20px;'>This code will expire in <strong>5 minutes</strong>.</p>" +
                        "<p>Please do not share this code with anyone.</p>" +
                        "<p style='color: #666; font-size: 12px; margin-top: 30px;'>" +
                        "If you didn't request this code, please ignore this email." +
                        "</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                otp
        );
    }
}
