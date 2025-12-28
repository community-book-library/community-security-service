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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users registerUser(UserDTO userDTO){
        Users user = new Users();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setCreatedBy(appTitle);
        LocalDateTime dt = LocalDateTime.now();
        user.setCreatedTimestamp(dt);
        Roles role = new Roles();
        if(userDTO.getRole() == null){
            role.setRoleId(1);
            role.setRole("USER");
        }
        else{
            role.setRoleId(3);
            role.setRole("MANAGER");
        }
        user.setRoles(role);
        Users response = commUserRepository.save(user);
        UserAuth userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setUsername(userDTO.getUsername());
        userAuth.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userAuth.setLoginStatus(UserAuth.LoginStatus.CREATED);
        userAuth.setInValidLoginAttempt(0);
        userAuth.setCreatedBy(appTitle);
        userAuth.setCreatedTimestamp(dt);
        commUserAuthRepository.save(userAuth);
        return response;
    }
}
