package com.example.quietconnect_backend.auth;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.quietconnect_backend.jwt.JwtUtil;
import com.example.quietconnect_backend.user.User;
import com.example.quietconnect_backend.user.UserRepository;
import com.example.quietconnect_backend.user.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService service;
    private final JwtUtil jwtUtil;
    private final UserRepository repo;
    private RedisTemplate redisTemplate;

    public OAuthSuccessHandler(UserService service, JwtUtil jwtUtil,UserRepository repo,RedisTemplate redisTemplate) {
        this.service = service;
        this.jwtUtil = jwtUtil;
        this.repo=repo;
        this.redisTemplate=redisTemplate;
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
        
        String baseUrl = "https://quiet-connect-frontend.vercel.app";

        String tempToken=UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("temp:" + tempToken,token,30,TimeUnit.SECONDS);

        System.out.println("your jwt" +token);

        String redirect=user.getUsername()!=null? "home":"set-username";
        response.sendRedirect(baseUrl + "/api/auth/exchange?token=" + tempToken + "&redirect=" + redirect);       
    }
}