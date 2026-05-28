package com.n8.emarket.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Long idBranch;

    public CustomUserDetails(Long id, String email, String password, String role, Long idBranch) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = Collections.singleton(new SimpleGrantedAuthority(role));
        this.idBranch = idBranch;
    }

    public Long getId() { return id; }
    public Long getIdBranch() { return idBranch; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return email; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}