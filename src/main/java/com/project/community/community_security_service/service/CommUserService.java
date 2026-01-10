package com.project.community.community_security_service.service;

import com.project.community.community_security_service.dto.UserDTO;
import com.project.community.community_security_service.entity.*;
import com.project.community.community_security_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    @Autowired
    private RegisterTokenRepository registerTokenRepository;

    @Autowired
    private CommUserRoleRepository commUserRoleRepository;

    public Users registerUser(UserDTO userDTO){
        Users user = new Users();
        RegisterToken reg = registerTokenRepository.findByToken(userDTO.getToken());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(reg.getUsername());
        user.setCreatedBy(appTitle);
        Roles role = commRoleRepository.findById(reg.getRoleId()).get();
        user.setRoles(role);
        Users response = commUserRepository.save(user);
        UserAuth userAuth = new UserAuth();
        userAuth.setUser(response);
        userAuth.setUsername(reg.getUsername());
        userAuth.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userAuth.setLoginStatus(UserAuth.LoginStatus.CREATED);
        userAuth.setInValidLoginAttempt(0);
        userAuth.setCreatedBy(appTitle);
        UserAuth authresponse = commUserAuthRepository.save(userAuth);
        UserCommunityRole userCommunityRole = new UserCommunityRole();
        userCommunityRole.setUserId(response.getId());
        userCommunityRole.setRoleId(reg.getRoleId());
        userCommunityRole.setCommunityId(reg.getCommunityId());
        userCommunityRole.setCreatedBy(appTitle);
        reg.setStatus("USED");
        commUserRoleRepository.save(userCommunityRole);
        return response;
    }
}
