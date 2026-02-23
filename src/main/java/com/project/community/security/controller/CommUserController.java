package com.project.community.security.controller;

import com.project.community.common.library.entity.RefreshToken;
import com.project.community.common.library.entity.Users;
import com.project.community.common.library.repository.CommUserAuthRepository;
import com.project.community.common.library.repository.RefreshTokenRepository;
import com.project.community.common.library.service.CommUserDetails;
import com.project.community.common.library.service.EmailService;
import com.project.community.common.library.service.JWTService;
import com.project.community.common.library.service.RefreshTokenService;
import com.project.community.security.dto.*;
import com.project.community.security.service.CommUserService;
import com.project.community.security.service.MFAAuthenticationToken;
import com.project.community.security.service.OTPService;
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
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
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

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Value("${app.title}")
    private String appTitle;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody UserDTO userDTO, @RequestHeader(value = "X-User-Email", required = false) String email) throws Exception {
        if(commUserService.findByUsername(userDTO,email)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already registered");
        }
        Users user = commUserService.registerUser(userDTO,email);
        return ResponseEntity.status(HttpStatus.CREATED).body("User is registered with " +user.getId());
    }

    @PostMapping("/admin")
    public ResponseEntity<?> adminLogin(@RequestBody AuthDTO authDTO){
        Authentication authentication =authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authDTO.getUsername(),authDTO.getPassword()));
        String accessToken = jwtService.generateAccessToken(authDTO.getUsername());
        String refreshToken = jwtService.generateRefreshToken(authDTO.getUsername());
        RefreshToken refToken = refreshTokenService.createRefreshToken(refreshToken, "m.sivasubramanian06@outlook.com");
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return ResponseEntity.ok(tokens);
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

            CommUserDetails userDetails = (CommUserDetails) userDetailsService.loadUserByUsername(username);
            Users user = userDetails.getUser();


            String refreshToken = jwtService.generateRefreshToken(username);
            RefreshToken refToken = refreshTokenService.createRefreshToken(refreshToken,username);
            String accessToken = jwtService.generateAccessToken(username);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

//            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
//                    .httpOnly(true)
//                    .secure(true) // Only HTTPS in production
//                    .path("/api/auth/refresh")
//                    .maxAge(30 * 24 * 60 * 60) // 30 days
//                    .sameSite("Strict")
//                    .build();

            return ResponseEntity.ok(new LoginResponse(
                    tokens,
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

    /**
     * REFRESH endpoint - Returns new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestBody RefreshTokenRequest request) {
        // Or get from cookie: @CookieValue("refreshToken") String refreshToken

        try{
            RefreshToken validRefreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
            String username = jwtService.extractUsername(request.getRefreshToken());
            String accessToken = jwtService.generateAccessToken(username);
            return ResponseEntity.ok(accessToken);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * LOGOUT endpoint - Revoke refresh token
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestBody RefreshTokenRequest request) {

        refreshTokenService.revokeRefreshToken(request.getRefreshToken());

//        // Clear cookie
//        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
//                .httpOnly(true)
//                .secure(true)
//                .path("/api/auth/refresh")
//                .maxAge(0)
//                .build();

        return ResponseEntity.ok()
                .body("Logged out successfully");
    }


}
