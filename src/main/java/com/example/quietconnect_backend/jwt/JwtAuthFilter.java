package com.example.quietconnect_backend.jwt;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ✅ Skip JWT filter for non-API & public endpoints
        if (
            !path.startsWith("/api") ||
            path.startsWith("/oauth2") ||
            path.startsWith("/login") ||
            path.startsWith("/public")
        ) {
            filterChain.doFilter(request, response);
            return;
        }


        String token = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if(token==null){
            String authHeader=request.getHeader("Authorization");
            if(authHeader!=null && authHeader.startsWith("Bearer ")){
                token=authHeader.substring(7);
            }

            System.out.println("your token from header "+token);
        }

        try {
            if (token != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

                    System.out.println("Token is received "+token);
                    System.out.println("Is valid "+jwtUtil.isValid(token));
                if (!jwtUtil.isValid(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Invalid JWT");
                    return;
                }

                String email = jwtUtil.extractEmail(token)
                                      .trim()
                                      .toLowerCase();
                
                System.out.println("Email extracted "+email);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                                     .setAuthentication(authToken);
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
