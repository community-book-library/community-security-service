package com.project.community.community_security_service.controller;

import com.project.community.community_security_service.DTO.*;
import com.project.community.community_security_service.entity.UserAuth;
import com.project.community.community_security_service.entity.Users;
import com.project.community.community_security_service.repository.CommUserAuthRepository;
import com.project.community.community_security_service.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
public class CommUserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CommUserService commUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private CommUserAuthRepository commUserAuthRepository;

    @Value("${app.title}")
    private String appTitle;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO){
        commUserService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("user created");
    }

    @PostMapping("/login")
    public ResponseEntity<?> generateToken(@RequestBody AuthDTO authDTO){
        try{
            Authentication authentication =authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getUsername(),authDTO.getPassword()));
            String token = jwtService.generateToken((UserDetails) authentication.getPrincipal());
            HttpHeaders headers =  new HttpHeaders();
            headers.add("Authorization", "Bearer " + token);
            return ResponseEntity.ok().headers(headers).body(new AuthResponse(token,"Login successful"));
        }
        catch(AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Username or password");
        }
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody OTPRequest request) {
        try {
            String otpSendType = request.getOtpSendType();
            var authToken = SecurityContextHolder.getContext().getAuthentication();

            if(authToken == null){
                return ResponseEntity.badRequest().body(new OTPResponse("Invalid JWT Token", false, 300L));
            }

            CommUserDetails userDetails = (CommUserDetails) authToken.getPrincipal();
            Users user = userDetails.getUser();
            String email = otpSendType.toUpperCase().equals("EMAIL") ? user.getEmail() : "";

            // Check if OTP already exists (rate limiting)
            if (otpService.otpExists(email)) {
                Long remainingTime = otpService.getOtpRemainingTime(email);
                return ResponseEntity.badRequest()
                        .body(new OTPResponse(
                                    "OTP already sent. Please wait before requesting a new one.",
                                    false,
                                    remainingTime
                        ));
            }

            // Generate OTP
            String otp = otpService.generateOtp(email);

            // Send email
            emailService.sendHtmlOtpEmail(email, otp);

            return ResponseEntity.ok(new OTPResponse(
                    "OTP sent successfully to " + email,
                    true,
                    300L // 5 minutes
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OTPResponse("Failed to send OTP: " + e.getMessage(), false, null));
        }
    }

    /**
     * Verify OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@Valid @RequestBody OTPVerification request) {

        String otp = request.getOtp();
        var authToken = SecurityContextHolder.getContext().getAuthentication();

        if(authToken == null){
            return ResponseEntity.badRequest().body(new OTPResponse("Invalid JWT Token", false, 300L));
        }

        CommUserDetails userDetails = (CommUserDetails) authToken.getPrincipal();
        Users user = userDetails.getUser();
        String email = user.getEmail();

        UserAuth userAuth = commUserAuthRepository.findByUsername(userDetails.getUsername());
        userAuth.setLoginStatus(UserAuth.LoginStatus.ACTIVE);
        LocalDateTime dt = LocalDateTime.now();
        userAuth.setUpdatedTimestamp(dt);
        userAuth.setUpdatedBy(appTitle);
        commUserAuthRepository.save(userAuth);

        if (otpService.validateOtp(email, otp)) {
            // OTP is valid - you can now authenticate the user or proceed with the action
            return ResponseEntity.ok(new OTPResponse("OTP verified successfully", true, null));
        } else {
            return ResponseEntity.badRequest()
                    .body(new OTPResponse("Invalid or expired OTP", false, null));
        }
    }




}
