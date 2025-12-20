package com.example.moviestest.security;

import com.example.moviestest.entity.User;
import com.example.moviestest.exception.AuthException;
import com.example.moviestest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String rawPassword = (String) authentication.getCredentials();

        User user = userService.findUserByUsername(username);
        if (user == null) {
            throw new BadCredentialsException("User not found");
        }
        if (!user.isEnabled()) {
            throw new AuthException("Пользователь заблокирован");
        }

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        List<GrantedAuthority> authorities = user.getRole() == null
                ? List.of()
                : List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole().name()));

        CustomPrincipal principal = new CustomPrincipal(user.getId(), user.getUsername());

        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
