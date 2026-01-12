package com.project.community.community_security_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String message;
    private boolean mfaRequired;
    private String username;
    private String tempToken; // For MFA verification
    private String mfaMethod; // EMAIL or SMS
}
