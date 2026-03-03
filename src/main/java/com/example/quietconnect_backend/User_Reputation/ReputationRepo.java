package com.example.quietconnect_backend.User_Reputation;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.dto.ReputationDto;
import com.example.quietconnect_backend.user.User;
import java.util.List;
import java.util.Optional;


@Repository
public interface ReputationRepo extends JpaRepository<Reputation,Long>{


    boolean existsByUserAndSnapShotDate(User user,LocalDate today);

    Optional<Reputation> findTopByUserOrderBySnapShotDateDesc(User user);


    Optional<Reputation> findTopByUserIdOrderBySnapShotDateDesc(Long userId);


    @Query("""
            SELECT new com.example.quietconnect_backend.dto.ReputationDto(
                r.user.id,r.snapShotDate,r.reputation_score )
                FROM Reputation r where r.user.id = :userId ORDER BY r.snapShotDate ASC
            """)
    List<ReputationDto> findByUserId(Long userId);

    @Query(
    value = "SELECT r.reputation_score " +
            "FROM reputation r " +
            "WHERE r.user_id = :userId " +
            "ORDER BY r.snap_shot_date DESC " +
            "LIMIT 1",
    nativeQuery = true
    )
    Long findLatestScoreByUserId(@Param("userId") Long userId);


    
}