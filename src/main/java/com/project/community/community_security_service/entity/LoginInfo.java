package com.project.community.community_security_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name="login_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="login_attempt")
    private int loginAttempt;

    @Column(name="last_login_time")
    private LocalDateTime lastLoginTime;

    public enum LoginStatus{
        CREATED,ACTIVE,LOCKED,DISABLED
    }

    @Enumerated(EnumType.STRING)
    @Column(name="login_status")
    private LoginStatus loginStatus;
}
