package com.project.community.community_security_service.service;

import com.project.community.community_security_service.entity.UserAuth;
import com.project.community.community_security_service.repository.CommUserAuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommUserDetailsService implements UserDetailsService {
    @Autowired
    private CommUserAuthRepository userAuthRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth userAuth = userAuthRepository.findByUsername(username);
        return new CommUserDetails(userAuth);
    }

    @Transactional
    public void enableMfa(String username, String method) {
        UserAuth user = userAuthRepository.findByUsername(username);
        user.setMfaEnabled(true);
        user.setMfaMethod(method);
        userAuthRepository.save(user);
    }

    @Transactional
    public void disableMfa(String username) {
        UserAuth user = userAuthRepository.findByUsername(username);
        user.setMfaEnabled(false);
        userAuthRepository.save(user);
    }

    @Transactional
    public void unlockAccount(String username) {
        UserAuth user = userAuthRepository.findByUsername(username);
        user.setAccountNonLocked(true);
        user.setInValidLoginAttempt(0);
        userAuthRepository.save(user);
    }
}
