package com.example.quietconnect_backend.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.dto.FrontReplyDto;

import java.util.List;


@Repository
public interface ReplyRepo extends JpaRepository<Reply,Long> {

    @Query("""
        select new com.example.quietconnect_backend.dto.FrontReplyDto(
        r.id,r.comment.id,r.user.username,r.user.bio,r.reply)from Reply r where r.comment= :comment """)
    List<FrontReplyDto> findByComment(Comment comment);
}