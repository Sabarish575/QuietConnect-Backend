package com.example.quietconnect_backend.Post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.dto.CommentDto;
import com.example.quietconnect_backend.dto.FrontCommentDto;
import com.example.quietconnect_backend.user.User;

@Repository
public interface CommentRepo extends JpaRepository<Comment,Long> {

    @Query("""
        select new com.example.quietconnect_backend.dto.FrontCommentDto(
        c.id,c.post.id,u.id,u.username,u.bio,c.comment,c.createdAt)
        from Comment c join c.user u where c.post= :post""")
    Page<FrontCommentDto> findByPost(Post post,Pageable pageable);

    @Query("""
        select new com.example.quietconnect_backend.dto.FrontCommentDto(
        c.id,c.post.id,u.id,u.username,u.bio,c.comment,c.createdAt)
        from Comment c join c.user u where u.id= :uid""")
    Page<FrontCommentDto> findByUserId(Long uid,Pageable pageable);

    @Query("""
    select new com.example.quietconnect_backend.dto.CommentDto(
    c.id,c.post.id,c.comment)
    from Comment c join c.user u where u.id= :userId""")
    List<CommentDto> findByUserId(Long userId);

    Long countByUserId(Long userId);
}
