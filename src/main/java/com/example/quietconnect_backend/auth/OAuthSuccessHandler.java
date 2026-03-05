package com.example.quietconnect_backend.auth;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.quietconnect_backend.jwt.JwtUtil;
import com.example.quietconnect_backend.user.User;
import com.example.quietconnect_backend.user.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService service;
    private final JwtUtil jwtUtil;

    public OAuthSuccessHandler(UserService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
        User user = service.processOauth2User(oauthUser);

        String token = jwtUtil.generateToken(
                user.getEmail().trim().toLowerCase()
        );

        ResponseCookie resCookie = ResponseCookie.from("token", token)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(7 * 24 * 60 * 60)
            .sameSite("Lax")
            .build();

        response.addHeader(HttpHeaders.SET_COOKIE, resCookie.toString());
        
        String baseUrl = "https://quiet-connect-frontend.vercel.app";

        if (user.getUsername() == null) {
            response.sendRedirect(baseUrl + "/set-username");
        } else {
            response.sendRedirect(baseUrl + "/home");
        }
    }
}