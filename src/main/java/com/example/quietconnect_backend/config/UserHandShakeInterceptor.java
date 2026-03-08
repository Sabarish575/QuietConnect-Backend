package com.example.quietconnect_backend.config;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.example.quietconnect_backend.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;


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

        String token=req.getParameter("token");

        if (token == null || token.isEmpty()) return false;

        try{
            String email=jwtUtil.extractEmail(token);
            attributes.put("email", email.toLowerCase());
            return true;
        }
        catch(Exception e){
            System.out.println("error" + e.getMessage());
            return false;
        }
 }
    
}
