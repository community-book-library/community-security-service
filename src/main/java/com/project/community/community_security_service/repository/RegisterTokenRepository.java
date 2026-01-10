package com.project.community.community_security_service.repository;


import com.project.community.community_security_service.entity.RegisterToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisterTokenRepository extends JpaRepository<RegisterToken,Integer> {
    RegisterToken findByToken(String token);
}
