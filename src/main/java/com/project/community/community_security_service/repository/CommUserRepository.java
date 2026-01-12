package com.project.community.community_security_service.repository;

import com.project.community.community_security_service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommUserRepository extends JpaRepository<Users,Integer> {
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);
}
