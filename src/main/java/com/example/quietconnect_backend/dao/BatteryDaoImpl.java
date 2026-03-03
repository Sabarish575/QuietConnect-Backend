package com.example.quietconnect_backend.dao;

import java.time.Duration;

import org.hibernate.engine.jdbc.mutation.spi.BatchKeyAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.dto.BatteryDto;

@Repository
public class BatteryDaoImpl implements BatteryDao {

    private static final String KEY_PREFIX="battery:user:";

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Integer addByid(Long id,Integer score) {
        redisTemplate.opsForValue().set(KEY_PREFIX + id.toString(), score,Duration.ofMinutes(5));
        return score;
    }

    @Override
    public Integer getByid(Long id) {
        return (Integer) redisTemplate.opsForValue().get(KEY_PREFIX + id.toString());
    }
    
}
