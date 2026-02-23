package com.project.community.security.service;

import com.project.community.common.library.entity.*;
import com.project.community.common.library.repository.*;
import com.project.community.security.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommUserServiceTest {

    @Mock
    private CommUserRepository commUserRepository;

    @Mock
    private CommUserAuthRepository commUserAuthRepository;

    @Mock
    private CommRoleRepository commRoleRepository;

    @Mock
    private RegisterTokenRepository registerTokenRepository;

    @Mock
    private CommUserRoleRepository commUserRoleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CommUserService commUserService;

    private UserDTO userDTO;
    private String username;
    private RegisterToken reg;
    private Users user;
    private UserAuth userAuth;
    private Roles roles;
    private UserCommunityRole userCommunityRole;
    private String appTitle;


    @BeforeEach
    public void setUp() throws IOException {
        appTitle = "community-library-auth";
        userDTO = new UserDTO("mani","mala","abc123","ad3d29af-6f96-4f3e-b124-c07434031512");
        username = "m.sivasubramanian06@gmail.com";

        ReflectionTestUtils.setField(commUserService,"appTitle", appTitle);

        reg =new RegisterToken(3,1,"$2a$10$WfD.uOpNXwNi/GtU6BUE4OqIMiqj8nnLsTU0Dun70iHeIpoGGe1E2",
                username,"ACTIVE",3,"community-info",null);

        roles = new Roles(3,"MANAGER");


        user = new Users();
        user.setId(2);
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(username);
        user.setRoles(roles);
        user.setCreatedBy(appTitle);

        userAuth = new UserAuth();
        userAuth.setUser(user);
        userAuth.setId(2);
        userAuth.setUsername(reg.getUsername());
        userAuth.setPassword("encoded-password");
        userAuth.setLoginStatus(UserAuth.LoginStatus.CREATED);
        userAuth.setInValidLoginAttempt(0);
        userAuth.setCreatedBy(appTitle);


        userCommunityRole = new UserCommunityRole();
        userCommunityRole.setUserId(user.getId());
        userCommunityRole.setRoleId(reg.getRoleId());
        userCommunityRole.setCommunityId(reg.getCommunityId());
    }

    @Test
    public void testRegisterUser_withValidInput_createsUser() throws Exception {

        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.of(reg));

        when(commRoleRepository.findById(3)).thenReturn(Optional.of(roles));

        when(commUserRepository.save(any(Users.class))).thenReturn(user);

        when(commUserAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);

        when(passwordEncoder.encode("abc123")).thenReturn("encoded-password");

        when(commUserRoleRepository.save(any(UserCommunityRole.class))).thenReturn(userCommunityRole);

        Users response = commUserService.registerUser(userDTO,username);
        assertNotNull(response);
        assertEquals("mani",response.getFirstName());
        assertEquals("mala",response.getLastName());
        assertEquals("USED",reg.getStatus());
        assertEquals(appTitle,user.getCreatedBy());
    }

    @Test
    public void testRegisterUser_VerifyUserCreation() throws Exception {
        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.of(reg));

        when(commRoleRepository.findById(3)).thenReturn(Optional.of(roles));

        when(commUserRepository.save(any(Users.class))).thenReturn(user);

        when(passwordEncoder.encode("abc123")).thenReturn("encoded-password");

        when(commUserAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);

        when(commUserRoleRepository.save(any(UserCommunityRole.class))).thenReturn(userCommunityRole);

        Users response = commUserService.registerUser(userDTO,username);
        verify(commUserRepository).save(argThat(user -> user.getFirstName().equals("mani")
        && user.getLastName().equals("mala")
        && user.getEmail().equals("m.sivasubramanian06@gmail.com")
        && user.getRoles().equals(roles)
        && user.getCreatedBy().equals(appTitle)));
    }

    @Test
    public void testRegisterUser_VerifyUserAuthCreation() throws Exception {
        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.of(reg));

        when(commRoleRepository.findById(3)).thenReturn(Optional.of(roles));

        when(commUserRepository.save(any(Users.class))).thenReturn(user);

        when(passwordEncoder.encode("abc123")).thenReturn("encoded-password");

        when(commUserAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);

        when(commUserRoleRepository.save(any(UserCommunityRole.class))).thenReturn(userCommunityRole);

        Users response = commUserService.registerUser(userDTO,username);
        verify(commUserAuthRepository).save(argThat(userAuth -> userAuth.getUsername().equals("m.sivasubramanian06@gmail.com")
                && userAuth.getUser() != null
                && userAuth.getPassword().equals("encoded-password")
                && userAuth.getLoginStatus() == UserAuth.LoginStatus.CREATED
                && userAuth.getInValidLoginAttempt() == 0
                && userAuth.getCreatedBy().equals(appTitle)));
    }

    @Test
    public void testRegisterUser_VerifyUserCommunityRoleCreation() throws Exception {
        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.of(reg));

        when(commRoleRepository.findById(3)).thenReturn(Optional.of(roles));

        when(commUserRepository.save(any(Users.class))).thenReturn(user);

        when(passwordEncoder.encode("abc123")).thenReturn("encoded-password");

        when(commUserAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);

        when(commUserRoleRepository.save(any(UserCommunityRole.class))).thenReturn(userCommunityRole);

        Users response = commUserService.registerUser(userDTO,username);
        verify(commUserRoleRepository).save(argThat(commUserRole -> commUserRole.getCommunityId() == 1
        && commUserRole.getUserId() == 2
        && commUserRole.getRoleId() == 3
        && commUserRole.getCreatedBy().equals(appTitle)));
    }

    @Test
    public void testRegisterUser_VerifyRegistryTokenStatusUpdated() throws Exception {
        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.of(reg));

        when(commRoleRepository.findById(3)).thenReturn(Optional.of(roles));

        when(commUserRepository.save(any(Users.class))).thenReturn(user);

        when(passwordEncoder.encode("abc123")).thenReturn("encoded-password");

        when(commUserAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);

        when(commUserRoleRepository.save(any(UserCommunityRole.class))).thenReturn(userCommunityRole);

        Users response = commUserService.registerUser(userDTO,username);
        assertEquals("USED",reg.getStatus());
        assertEquals(appTitle,reg.getUpdatedBy());

    }


    @Test
    void registerUser_VerifyPasswordEncoding() throws Exception {
        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.of(reg));

        when(commRoleRepository.findById(3)).thenReturn(Optional.of(roles));

        when(commUserRepository.save(any(Users.class))).thenReturn(user);

        when(passwordEncoder.encode("abc123")).thenReturn("encoded-password");

        when(commUserAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);

        when(commUserRoleRepository.save(any(UserCommunityRole.class))).thenReturn(userCommunityRole);

        Users response = commUserService.registerUser(userDTO,username);
        verify(passwordEncoder).encode("abc123");
    }


    @Test
    void registerUser_WhenRegisterTokenNotFound_ThrowsException() {

        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> commUserService.registerUser(userDTO,username));

        verify(commRoleRepository,never()).findById(anyInt());
        verify(commUserRepository,never()).save(any(Users.class));
        verify(commUserAuthRepository, never()).save(any(UserAuth.class));
        verify(commUserRoleRepository,never()).save(any(UserCommunityRole.class));

    }

    @Test
    void registerUser_WhenRoleNotFound_ThrowsException() {
        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.of(reg));

        when(commRoleRepository.findById(100)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> commUserService.registerUser(userDTO,username));

        verify(commUserRepository,never()).save(any(Users.class));
        verify(commUserAuthRepository, never()).save(any(UserAuth.class));
        verify(commUserRoleRepository,never()).save(any(UserCommunityRole.class));
    }

    @Test
    public void testRegisterUser_allRepoMethodsCalled() throws Exception {
        when(registerTokenRepository.findByUsername(username))
                .thenReturn(Optional.of(reg));

        when(commRoleRepository.findById(3)).thenReturn(Optional.of(roles));

        when(commUserRepository.save(any(Users.class))).thenReturn(user);

        when(passwordEncoder.encode("abc123")).thenReturn("encoded-password");

        when(commUserAuthRepository.save(any(UserAuth.class))).thenReturn(userAuth);

        when(commUserRoleRepository.save(any(UserCommunityRole.class))).thenReturn(userCommunityRole);

        Users response = commUserService.registerUser(userDTO,username);
        verify(commUserRepository,times(1)).save(any(Users.class));
        verify(commUserAuthRepository,times(1)).save(any(UserAuth.class));
        verify(commUserRoleRepository,times(1)).save(any(UserCommunityRole.class));

    }


}
