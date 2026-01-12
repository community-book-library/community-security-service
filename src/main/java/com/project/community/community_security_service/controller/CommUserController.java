package com.project.community.community_security_service.controller;

import com.project.community.community_security_service.dto.*;
import com.project.community.community_security_service.entity.Users;
import com.project.community.community_security_service.repository.CommUserAuthRepository;
import com.project.community.community_security_service.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        if(commUserService.findByUsername(userDTO)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already registered");
        }
        Users user = commUserService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("User is registered with " +user.getId());
    }

    @PostMapping("/admin")
    public ResponseEntity<?> adminLogin(@RequestBody AuthDTO authDTO){
        Authentication authentication =authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(),authDTO.getPassword()));
        String token = jwtService.generateToken(authDTO.getUsername(),false);
        return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(token,"Valid admin login"));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> generateToken(@RequestBody AuthDTO authDTO){
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getUsername(),authDTO.getPassword()));
            CommUserDetails userDetails = ((CommUserDetails) authentication.getPrincipal());

                // Generate OTP
            String otp = otpService.generateOtp(userDetails.getUsername());
            if ("EMAIL".equals(userDetails.getMfaMethod())) {
                    emailService.sendOtpEmail(
                            userDetails.getUsername(),
                            otp
                    );
                }
                // Generate temporary token
            String tempToken = jwtService.generateTempToken(userDetails.getUsername());
            return ResponseEntity.ok(new LoginResponse(
                        null,
                        "OTP sent to your " + userDetails.getMfaMethod().toLowerCase() +
                                ". Please verify to complete login.",
                        true,
                        userDetails.getUsername(),
                        tempToken,
                        userDetails.getMfaMethod()
                ));
        }
        catch(AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(
                    null,
                    "Invalid username or password",
                    false,
                    null,
                    null,
                    null
            ));
        }
    }

    /**
     * Resend OTP
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<LoginResponse> resendOtp(@Valid HttpServletRequest request) {
        try {
            final String authHeader = request.getHeader("Authorization");
            final String jwt = authHeader.substring(7);
            String username = jwtService.extractUsername(jwt);
            CommUserDetails userDetails = (CommUserDetails) userDetailsService.loadUserByUsername(username);

            if(otpService.otpExists(userDetails.getUsername())){
                otpService.deleteOtp(userDetails.getUsername());
            }

            // Generate new OTP
            String otp = otpService.generateOtp(userDetails.getUsername());

            // Send OTP
            if ("EMAIL".equals(userDetails.getMfaMethod())) {
                emailService.sendOtpEmail(userDetails.getUsername(),  otp);
            }

            return ResponseEntity.ok(new LoginResponse(
                    null,
                    "OTP resent to your " + userDetails.getMfaMethod().toLowerCase() +
                            ". Please verify again to complete login.",
                    true,
                    userDetails.getUsername(),
                    jwt,
                    userDetails.getMfaMethod()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new LoginResponse(
                    null,
                    "Internal server error",
                    false,
                    null,
                    null,
                    null
            ));
        }
    }




    /**
     * Verify OTP
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponse> verifyOtp(@Valid @RequestBody MFAVerificationRequest request) {

        try {
            // Validate temp token
            String username = jwtService.extractUsername(request.getTempToken());

            if (!jwtService.isTempToken(request.getTempToken())) {
                return ResponseEntity.badRequest()
                        .body(new LoginResponse(
                                null,
                                "Invalid temporary token",
                                false,
                                null,
                                null,
                                null
                        ));
            }

            // Authenticate OTP
            MFAAuthenticationToken mfaToken = new MFAAuthenticationToken(username, request.getOtp());
            Authentication authentication = authenticationManager.authenticate(mfaToken);

            // Generate full access token
            String token = jwtService.generateToken(username, true);

            return ResponseEntity.ok(new LoginResponse(
                    token,
                    "OTP verification successful. Login complete.",
                    false,
                    username,
                    null,
                    null
            ));

        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(new LoginResponse(
                            null,
                            e.getMessage(),
                            false,
                            null,
                            null,
                            null
                    ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(
                            null,
                            e.getMessage(),
                            false,
                            null,
                            null,
                            null
                    ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse(
                            null,
                            "Verification failed: " + e.getMessage(),
                            false,
                            null,
                            null,
                            null
                    ));
        }
    }




}
