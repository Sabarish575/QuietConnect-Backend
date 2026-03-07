package com.example.quietconnect_backend.jwt_controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;

import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class ExchangeToken {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/authExchange")
    public ResponseEntity<?> exchange(@RequestParam String token) {
        Object value=redisTemplate.opsForValue().get("temp:" + token);

        if(value==null){
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        String jwt=value.toString();
        redisTemplate.delete("temp:" + token);
        return ResponseEntity.ok(java.util.Map.of("jwt",jwt));
    }
    
    
}
