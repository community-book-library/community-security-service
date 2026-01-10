package com.project.community.community_security_service.repository;

import com.project.community.community_security_service.entity.RegisterToken;
import com.project.community.community_security_service.entity.UserCommunityRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommUserRoleRepository extends JpaRepository<UserCommunityRole,Integer> {


}
