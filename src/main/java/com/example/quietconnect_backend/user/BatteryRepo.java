package com.example.quietconnect_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.dto.BatteryDto;

import jakarta.transaction.Transactional;

@Repository
public interface BatteryRepo extends JpaRepository<Battery,Long> {


    @Query("""
            SELECT b.batteryPercentage
             FROM Battery b WHERE b.user.id = :userId
            """)
    Integer findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE Battery b SET b.batteryPercentage = :score WHERE b.user = :user""")
    Integer updateBattery(User user,Integer score);
    
}
