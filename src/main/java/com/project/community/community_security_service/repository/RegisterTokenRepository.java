package com.project.community.community_security_service.repository;


import com.project.community.community_security_service.entity.RegisterToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegisterTokenRepository extends JpaRepository<RegisterToken,Integer> {
    Optional<RegisterToken> findByToken(String token);
}
