package com.project.community.community_security_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "login_status")
    private LoginStatus loginStatus;

    @Column(name = "invalid_login_attempt")
    private int inValidLoginAttempt;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_by")
    private String updatedBy;

    @Column(name="created_timestamp")
    private LocalDateTime createdTimestamp;

    @Column(name="updated_timestamp")
    private LocalDateTime updatedTimestamp;
}
