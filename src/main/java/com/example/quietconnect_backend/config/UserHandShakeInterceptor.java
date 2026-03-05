package com.example.quietconnect_backend.config;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.quietconnect_backend.jwt.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Component
public class UserHandShakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    public UserHandShakeInterceptor(JwtUtil ju){
        this.jwtUtil=ju;
    }



    @Override
    public void afterHandshake(ServerHttpRequest arg0, ServerHttpResponse arg1, WebSocketHandler arg2,
            @Nullable Exception arg3) {
    }


    //extracting the email before connection is made 

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

                if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }

        HttpServletRequest req = servletRequest.getServletRequest();


        if (req.getCookies() == null) return false;

        for (Cookie cookie : req.getCookies()) {
            if ("token".equals(cookie.getName())) {
                String token = cookie.getValue();
                String email =jwtUtil.extractEmail(token); 

                System.out.println("interceptor extracted your email"+ email);
                
                attributes.put("email", email.toLowerCase());
                return true;
            }
        }

        return false;   
 }
    
}
