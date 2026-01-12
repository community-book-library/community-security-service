package com.project.community.community_security_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name="user_auth")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name="user_id")
    private Users user;

    private String username;
    private String password;

    public enum LoginStatus{
        CREATED, LOCKED, ACTIVE, DISABLED
    }

    @Column(name = "mfa_enabled")
    private boolean mfaEnabled = true;

    @Column(name = "mfa_method")
    private String mfaMethod = "EMAIL";

    @Enumerated(EnumType.STRING)
    @Column(name = "login_status")
    private LoginStatus loginStatus;

    @Column(name = "invalid_login_attempt")
    private int inValidLoginAttempt;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_by")
    private String updatedBy;


    private boolean enabled = true;

    @Column(name="account_non_locked")
    private boolean accountNonLocked = true;


}
