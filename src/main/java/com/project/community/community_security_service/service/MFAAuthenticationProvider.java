package com.project.community.community_security_service.service;

import com.project.community.community_security_service.entity.UserAuth;
import com.project.community.community_security_service.repository.CommUserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class MFAAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private CommUserAuthRepository userRepository;

    @Autowired
    private OTPService otpService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MFAAuthenticationToken mfaToken = (MFAAuthenticationToken) authentication;

        String username = (String) mfaToken.getPrincipal();
        String otp = (String) mfaToken.getCredentials();

        // Load user
        UserAuth user = userRepository.findByUsername(username);
        if(user.getLoginStatus().toString().equals("CREATED")){
            user.setLoginStatus(UserAuth.LoginStatus.ACTIVE);
        }

        // Check if account is locked
        if (!user.isAccountNonLocked()) {
            throw new LockedException("Account is locked due to multiple failed attempts");
        }

        // Check if MFA is enabled
        if (!user.isMfaEnabled()) {
            throw new BadCredentialsException("MFA not enabled for user");
        }

        // Verify OTP
        boolean isValid = otpService.validateOtp(username, otp);

        if (!isValid) {
            // Increment failed attempts
            user.setInValidLoginAttempt(user.getInValidLoginAttempt() + 1);

            // Lock account after 5 failed attempts
            if (user.getInValidLoginAttempt() >= 5) {
                user.setAccountNonLocked(false);
                userRepository.save(user);
                throw new LockedException("Account locked due to multiple failed OTP attempts");
            }

            userRepository.save(user);


        }

        // Reset failed attempts on successful verification
        user.setInValidLoginAttempt(0);
        userRepository.save(user);

        // Return authenticated token
        return new MFAAuthenticationToken(
                username,
                otp,
                Collections.singletonList(new SimpleGrantedAuthority(user.getUser().getRoles().getRole()))
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MFAAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
