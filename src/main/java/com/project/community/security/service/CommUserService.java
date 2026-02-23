package com.project.community.security.service;

import com.project.community.common.library.entity.*;
import com.project.community.common.library.repository.*;
import com.project.community.security.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    @Transactional
    public Users registerUser(UserDTO userDTO,String username) throws Exception {
        Users user = new Users();
        String hashed = userDTO.getToken();
        Optional<RegisterToken> regis = registerTokenRepository.findByUsername(username);
        if(regis.isEmpty()){
            throw new Exception("Invalid User - Registry Token not present");
        }

        RegisterToken reg = regis.get();
        Optional<Roles> rol = commRoleRepository.findById(reg.getRoleId());
        if(rol.isEmpty()){
            throw new Exception("Invalid User - Role not present");
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(username);
        user.setCreatedBy(appTitle);


        Roles role = rol.get();
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
        reg.setUpdatedBy(appTitle);
        commUserRoleRepository.save(userCommunityRole);
        return response;
    }

    public boolean findByUsername(UserDTO userDTO,String username) {
        String hashed = userDTO.getToken();
        Optional<Users> user = commUserRepository.findByEmail(username);
        return user.isPresent();
    }
}
