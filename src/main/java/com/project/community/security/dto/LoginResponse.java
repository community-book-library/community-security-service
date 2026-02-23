package com.project.community.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Map<String,String> tokens;
    private String message;
    private boolean mfaRequired;
    private String username;
    private String tempToken; // For MFA verification
    private String mfaMethod; // EMAIL or SMS
}
