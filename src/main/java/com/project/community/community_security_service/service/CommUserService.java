package com.project.community.community_security_service.service;

import com.project.community.community_security_service.DTO.UserDTO;
import com.project.community.community_security_service.entity.Roles;
import com.project.community.community_security_service.entity.UserAuth;
import com.project.community.community_security_service.entity.Users;
import com.project.community.community_security_service.repository.CommRoleRepository;
import com.project.community.community_security_service.repository.CommUserAuthRepository;
import com.project.community.community_security_service.repository.CommUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommUserService {

    @Autowired
    private CommUserRepository commUserRepository;

    @Autowired
    private CommUserAuthRepository commUserAuthRepository;


    @Autowired
    private CommRoleRepository commRoleRepository;

    @Value("${app.title}")
    private String appTitle;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    public void registerUser(UserDTO userDTO){
        Users user = new Users();

        List<Roles> roles = commRoleRepository.findAll();
        Roles role = roles.stream()
                .filter(r -> r.getRole().equals("USER")).findFirst().get();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setCreatedBy(appTitle);
        LocalDateTime dt = LocalDateTime.now();
        user.setCreatedTimestamp(dt);
        user.setRoles(role);
        commUserRepository.save(user);
        UserAuth userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setUsername(userDTO.getUsername());
        PasswordEncoder passEncoder = passwordEncoder();
        userAuth.setPassword(passEncoder.encode(userDTO.getPassword()));
        userAuth.setCreatedBy(appTitle);
        userAuth.setCreatedTimestamp(dt);
        commUserAuthRepository.save(userAuth);
    }
}
