package com.project.community.community_security_service.controller;

import com.project.community.community_security_service.DTO.UserDTO;
import com.project.community.community_security_service.service.CommUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class CommUserController {

    @Autowired
    private CommUserService commUserService;

    @PostMapping("/register")
    public void register(@RequestBody UserDTO userDTO){
        commUserService.registerUser(userDTO);
    }

}
