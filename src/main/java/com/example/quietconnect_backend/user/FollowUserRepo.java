package com.example.quietconnect_backend.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowUserRepo extends JpaRepository<FollowUser,Long> {
    void deleteByFollowerAndFollowing(User followerid,User followingId);
    boolean existsByFollowerAndFollowing(User followerid,User followingId);
    Long countByFollower(User user);
    Long countByFollowing(User user);
}
