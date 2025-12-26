package com.project.community.community_security_service.service;

import com.project.community.community_security_service.entity.UserAuth;
import com.project.community.community_security_service.repository.CommUserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CommUserDetailsService implements UserDetailsService {
    @Autowired
    private CommUserAuthRepository userAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth userAuth = userAuthRepository.findByUsername(username);
        return new CommUserDetails(userAuth);
    }
}
