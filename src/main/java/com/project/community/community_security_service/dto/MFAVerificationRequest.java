package com.project.community.community_security_service.dto;

import lombok.Data;

@Data
public class MFAVerificationRequest {
    private String tempToken;
    private String otp;
}
