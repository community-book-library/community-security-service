package com.project.community.community_security_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany
    @JoinColumn(name="login_info_id")
    private List<LoginInfo> loginInfo;


    private String username;
    private String password;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_by")
    private String updatedBy;

    @Column(name="created_timestamp")
    private LocalDateTime createdTimestamp;

    @Column(name="updated_timestamp")
    private LocalDateTime updatedTimestamp;
}
