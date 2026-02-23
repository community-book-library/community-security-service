package com.project.community.security.dto;

import lombok.Data;

@Data
public class MFAVerificationRequest {
    private String tempToken;
    private String otp;
}
