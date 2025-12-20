package com.example.moviestest.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHandler jwtHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (token != null) {
                Claims claims = jwtHandler.parseClaims(token);
                if (!jwtHandler.isTokenExpired(claims)) {
                    String subject = claims.getSubject();
                    String username = claims.get("username", String.class);
                    String role = claims.get("role", String.class);

                    CustomPrincipal principal;
                    try {
                        Long userId = Long.parseLong(subject);
                        principal = new CustomPrincipal(userId, username);
                    } catch (Exception ex) {
                        principal = new CustomPrincipal(null, username);
                    }

                    List authorities = role == null ? List.of() :
                            List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception ex) {
            logger.debug("Invalid JWT: {}", ex.getCause());
        }
        filterChain.doFilter(request, response);
    }


    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("token".equals(c.getName())) return c.getValue();
            }
        }

        return null;
    }
}
