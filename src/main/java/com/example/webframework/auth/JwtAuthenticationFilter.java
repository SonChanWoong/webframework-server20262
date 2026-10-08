package com.example.webframework.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtDecoder jwtDecoder;

    public JwtAuthenticationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = null;
        var cookie = request.getCookies();
        if (cookie != null) {
            for (var item : cookie) {
                if ("accessToken".equals(item.getName())) {
                    authorization = "Bearer " + item.getValue();
                    break;
                }
            }
        }
        if (authorization == null) {
            authorization = request.getHeader("Authorization");
        }
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                Jwt claims = jwtDecoder.decode(authorization.substring(7));
                var authentication = new UsernamePasswordAuthenticationToken(claims, null, Collections.emptyList());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException ignored) {
                // Invalid or expired tokens are treated as unauthenticated by Spring Security.
            }
        }
        filterChain.doFilter(request, response);
    }
}
