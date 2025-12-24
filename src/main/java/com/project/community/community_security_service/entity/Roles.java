package com.project.community.community_security_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity(name="user_roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roleId;


    public enum Role{
        ADMIN,MANAGER,USER
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

}
