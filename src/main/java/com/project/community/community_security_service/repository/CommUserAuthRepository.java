package com.project.community.community_security_service.repository;

import com.project.community.community_security_service.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommUserAuthRepository extends JpaRepository<UserAuth,Integer> {
}
