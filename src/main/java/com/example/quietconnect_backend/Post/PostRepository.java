package com.example.quietconnect_backend.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.example.quietconnect_backend.Community.Communities;



@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    
    Page<Post> findByCreatedById(Long userId,Pageable pageable);

    Page<Post> findByCommunity(Communities community,Pageable pageable);

    @Query("""
        SELECT p FROM Post p JOIN p.community c 
        JOIN c.communityMember cm WHERE cm.user.id = :userId ORDER BY p.createdAt DESC
    """)
    Page<Post> findUserFeed(Long userId,Pageable pageable);

    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);




}
