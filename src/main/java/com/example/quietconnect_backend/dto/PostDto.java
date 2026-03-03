package com.example.quietconnect_backend.dto;

import java.time.LocalTime;

import com.example.quietconnect_backend.Post.Post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {

    private Long id;
    private String title;
    private String description;
    private Long communityId;
    private Long createdById;
    private LocalTime createdAt;
    private long likeCount;
    private long commentCount;

    public PostDto(Post post){
        this.id=post.getId();
        this.title=post.getTitle();
        this.description=post.getDescription();
        this.communityId=post.getCommunity().getId();
        this.createdById=post.getCreatedBy().getId();
        this.createdAt=post.getCreatedAt();
        this.likeCount=post.getLikes().size();
        this.commentCount=post.getComments().size();
    }


    
}
