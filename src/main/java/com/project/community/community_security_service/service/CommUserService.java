package com.project.community.community_security_service.service;

import com.project.community.community_security_service.DTO.UserDTO;
import com.project.community.community_security_service.entity.LoginInfo;
import com.project.community.community_security_service.entity.Roles;
import com.project.community.community_security_service.entity.UserAuth;
import com.project.community.community_security_service.entity.Users;
import com.project.community.community_security_service.repository.CommRoleRepository;
import com.project.community.community_security_service.repository.CommUserAuthRepository;
import com.project.community.community_security_service.repository.CommUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommUserService {

    @Autowired
    private CommUserRepository commUserRepository;

    @Autowired
    private CommUserAuthRepository commUserAuthRepository;


    @Autowired
    private CommRoleRepository commRoleRepository;
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }


    public void registerUser(UserDTO userDTO){
        Users user = new Users();

        List<Roles> roles = commRoleRepository.findAll();
        Roles role = roles.stream()
                .filter(r -> r.getRole().name().equals(Roles.Role.USER.name()))
                .findFirst().get();

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setCreatedBy("application");
        LocalDateTime dt = LocalDateTime.now();
        user.setCreatedTimestamp(dt);
        user.setRoles(role);
        commUserRepository.save(user);
        UserAuth userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setUsername(userDTO.getUsername());
//        PasswordEncoder passEncoder = passwordEncoder();
        userAuth.setPassword(userDTO.getPassword());
        userAuth.setCreatedBy("application");
        userAuth.setCreatedTimestamp(dt);
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setLoginAttempt(1);
        loginInfo.setLoginStatus(LoginInfo.LoginStatus.CREATED);
        loginInfo.setLastLoginTime(dt);
        List<LoginInfo> list = new ArrayList<>();
        list.add(loginInfo);
        userAuth.setLoginInfo(list);
        commUserAuthRepository.save(userAuth);
    }
}
