package com.project.community.security;

import com.project.community.common.library.entity.Users;
import com.project.community.common.library.repository.CommUserAuthRepository;
import com.project.community.common.library.repository.RefreshTokenRepository;
import com.project.community.common.library.service.CommUserDetailsService;
import com.project.community.common.library.service.EmailService;
import com.project.community.common.library.service.JWTService;
import com.project.community.common.library.service.RefreshTokenService;
import com.project.community.security.dto.UserDTO;
import com.project.community.security.service.CommUserService;
import com.project.community.security.service.OTPService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc()
@TestPropertySource(properties = "classpath:application.properties")
class CommunitySecurityServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CommUserService commUserService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private JWTService jwtService;


    @MockitoBean
    private CommUserDetailsService commUserDetailsService;

    @MockitoBean
    private CommUserAuthRepository commUserAuthRepository;

    @MockitoBean
    private OTPService otpService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private EmailService emailService;


    @Test
    public void testRegister_User_withValidInput() throws Exception {
        UserDTO userDTO = new UserDTO("mani","mala","abc123","ad3d29af-6f96-4f3e-b124-c07434031512");

        Users mockUser = new Users();
        mockUser.setId(1);

        when(commUserService.findByUsername(any(UserDTO.class), anyString()))
                .thenReturn(false);
        when(commUserService.registerUser(any(UserDTO.class), anyString()))
                .thenReturn(mockUser);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO))
                        .header("X-User-Email","m.sivasubramanian06@gmail.com"))
                        .andExpect(status().isCreated())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().string("User is registered with 1"));

        verify(commUserService).findByUsername(any(UserDTO.class), eq("m.sivasubramanian06@gmail.com"));
        verify(commUserService).registerUser(any(UserDTO.class), eq("m.sivasubramanian06@gmail.com"));
    }

}
