package com.project.community.community_security_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "register_token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "community_id")
    private int communityId;

    @Column(name="token")
    private String token;

    @Column(name="username")
    private String username;

    @Column(name="status")
    private String status;

    @Column(name="role_id")
    private int roleId;
}
