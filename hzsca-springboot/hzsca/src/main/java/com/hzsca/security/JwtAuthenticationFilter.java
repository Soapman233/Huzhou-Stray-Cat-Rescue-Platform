package com.hzsca.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token = null;
        if (header != null && header.startsWith("Bearer ")) {
            token = header.substring("Bearer ".length()).trim();
        }
        if (token == null || token.isBlank()) {
            String q = request.getParameter("access_token");
            if (q != null && !q.isBlank()) {
                token = q.trim();
            }
        }
        if (token != null && !token.isBlank()) {
            try {
                Jws<Claims> jws = jwtService.parse(token);
                Claims claims = jws.getPayload();
                String sub = claims.getSubject();
                String role = claims.get("role", String.class);
                String ptype = claims.get("ptype", String.class);
                if (sub != null && role != null) {
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                    if ("volunteer".equalsIgnoreCase(role)) {
                        authorities.add(new SimpleGrantedAuthority("ROLE_user"));
                    }
                    AuthPrincipal principal = new AuthPrincipal(Long.parseLong(sub), ptype == null ? "unknown" : ptype);
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(principal, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception ignored) {
                SecurityContextHolder.clearContext();
            }
        }
        filterChain.doFilter(request, response);
    }
}
