package com.example.quietconnect_backend.Community;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.dto.CommunityDetails;
import com.example.quietconnect_backend.dto.UserCreatedCommunity;
import com.example.quietconnect_backend.dto.CommunityDetails;

@Repository
public interface CommunityRepository extends JpaRepository<Communities, Long> {

    boolean existsByCommunityTitle(String title);

    @Query("""
    SELECT new com.example.quietconnect_backend.dto.UserCreatedCommunity(
        c.id,
        c.communityTitle,
        c.communityBio,
        COUNT(cm.id),
        c.createdAt
    )
    FROM Communities c
    LEFT JOIN c.communityMember cm
    WHERE c.createdBy.id = :userId
    GROUP BY c.id, c.communityTitle, c.communityBio, c.createdAt
    """)
    List<UserCreatedCommunity> findByCreatedById(@Param("userId") Long userId);

    @Query("""
        SELECT c FROM Communities c
        WHERE LOWER(c.communityTitle) LIKE LOWER(CONCAT('%', :value , '%'))
    """)
    List<Communities> findByWord(@Param("value")  String value);

    @Query("""
        SELECT c FROM Communities c ORDER BY SIZE(c.communityMember) DESC
        """)
    Page<Communities> findByPopularity(Pageable pageable);

        @Query("""
        SELECT c FROM Communities c
        WHERE LOWER(c.communityTitle) LIKE LOWER(CONCAT('%', :value , '%'))
        ORDER BY SIZE(c.communityMember) DESC
        """)

    List<Communities> findByWordByPopular(@Param("value") String value);



}

