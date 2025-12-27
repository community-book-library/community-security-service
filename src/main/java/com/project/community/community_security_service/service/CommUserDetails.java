package com.project.community.community_security_service.service;

import com.project.community.community_security_service.entity.Roles;
import com.project.community.community_security_service.entity.UserAuth;
import com.project.community.community_security_service.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CommUserDetails implements UserDetails {
    private String username;
    private String password;
    private Users user;
    private boolean enabled;
    private Collection<? extends GrantedAuthority> authorities;

    public CommUserDetails(UserAuth userAuth) {
        this.username = userAuth.getUsername();
        this.password = userAuth.getPassword();
        this.user = userAuth.getUser();
        this.enabled = true;
    }

    public Users getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Roles role = user.getRoles();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getRole());
        return List.of(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
