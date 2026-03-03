package com.example.quietconnect_backend.Community;

import com.example.quietconnect_backend.dto.UserJoinedCommunity;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommunityMemberRepo extends JpaRepository<CommunityMember, Long> {

    @Query("""
        SELECT new com.example.quietconnect_backend.dto.UserJoinedCommunity(
            cm.communities.id,
            cm.communities.communityTitle,  
            cm.communities.communityBio,     
            cm.joinedAt
        )
        FROM CommunityMember cm
        WHERE cm.user.id = :userId
    """)
    List<UserJoinedCommunity> findJoinedByUserId(@Param("userId") Long userId);

    Optional<CommunityMember> findByCommunitiesIdAndUserId(Long communityId, Long userId);
    Long countByCommunitiesId(Long communityId);


    boolean existsByCommunitiesIdAndUserId(Long comId,Long userId);

    boolean existsByUserId(Long userId);
}
