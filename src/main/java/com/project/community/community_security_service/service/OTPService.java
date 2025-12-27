package com.project.community.community_security_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
public class OTPService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${otp.expiration.minutes:5}")
    private long otpExpirationMinutes;

    @Value("${otp.length:6}")
    private int otpLength;

    private static final String OTP_PREFIX = "otp:";
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generate OTP and store in Redis
     */
    public String generateOtp(String email) {
        String otp = generateRandomOtp();
        String key = OTP_PREFIX + email;

        // Store OTP in Redis with expiration
        redisTemplate.opsForValue().set(key, otp, otpExpirationMinutes, TimeUnit.MINUTES);

        return otp;
    }

    /**
     * Validate OTP
     */
    public boolean validateOtp(String email, String otp) {
        String key = OTP_PREFIX + email;
        String storedOtp = redisTemplate.opsForValue().get(key);

        if (storedOtp != null && storedOtp.equals(otp)) {
            // Delete OTP after successful validation
            redisTemplate.delete(key);
            return true;
        }

        return false;
    }

    /**
     * Check if OTP exists for email
     */
    public boolean otpExists(String email) {
        String key = OTP_PREFIX + email;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Delete OTP
     */
    public void deleteOtp(String email) {
        String key = OTP_PREFIX + email;
        redisTemplate.delete(key);
    }

    /**
     * Get remaining time for OTP
     */
    public Long getOtpRemainingTime(String email) {
        String key = OTP_PREFIX + email;
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * Generate random numeric OTP
     */
    private String generateRandomOtp() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
