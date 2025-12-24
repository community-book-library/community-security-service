package com.project.community.community_security_service.repository;

import com.project.community.community_security_service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommUserRepository extends JpaRepository<Users,Integer> {
}
