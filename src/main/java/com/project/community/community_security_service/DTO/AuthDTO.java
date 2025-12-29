package com.project.community.community_security_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthDTO {
    //Arun: package name cannot be upper case
    private String username;
    private String password;
}
