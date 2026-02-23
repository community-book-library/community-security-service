package com.project.community.security.service;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MFAAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private final String otp;

    // Before authentication
    public MFAAuthenticationToken(Object principal, String otp) {
        super((Collection<? extends GrantedAuthority>) null);
        this.principal = principal;
        this.otp = otp;
        setAuthenticated(false);
    }

    // After authentication
    public MFAAuthenticationToken(Object principal, String otp,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.otp = otp;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return otp;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
