package com.project.community.community_security_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name="community_user_role_association")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCommunityRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="user_id")
    private int userId;

    @Column(name="role_id")
    private int roleId;

    @Column(name="community_id")
    private int communityId;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="updated_by")
    private String updatedBy;

}
