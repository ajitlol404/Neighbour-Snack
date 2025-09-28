package com.akn.ns.neighbour_snack_be.security;

import com.akn.ns.neighbour_snack_be.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static java.lang.Boolean.TRUE;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    @Override
    public boolean isAccountNonExpired() {
        return TRUE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return TRUE;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return TRUE;
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

}
