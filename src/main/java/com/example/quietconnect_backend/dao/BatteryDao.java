package com.example.quietconnect_backend.dao;

import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.dto.BatteryDto;

@Repository
public interface BatteryDao { 
    Integer getByid(Long id);
    Integer addByid(Long id,Integer score);
}
