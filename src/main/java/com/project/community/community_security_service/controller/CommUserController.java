package com.project.community.community_security_service.controller;

import com.project.community.community_security_service.DTO.AuthDTO;
import com.project.community.community_security_service.DTO.UserDTO;
import com.project.community.community_security_service.service.CommUserService;
import com.project.community.community_security_service.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class CommUserController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CommUserService commUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody UserDTO userDTO){
        commUserService.registerUser(userDTO);
        return "user created";
    }

    @PostMapping("/login")
    public String generateToken(@RequestBody AuthDTO authDTO){
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getUsername(),authDTO.getPassword()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(authDTO.getUsername());
            String token = jwtService.generateToken(userDetails);
            return token;
        }
        catch(AuthenticationException e) {
            return "Unauthorized";
        }
    }



}
