package com.example.quietconnect_backend.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepo extends JpaRepository<Like,Long> {
 
    Long countByPostId(Long postId);
    Long countByUserId(Long userId);

    Like findByPostIdAndUserId(Long postid,Long userid);

    boolean existsByPostIdAndUserId(Long postid,Long userid);
}
