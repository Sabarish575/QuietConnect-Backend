package com.example.quietconnect_backend.dto;

import java.time.LocalTime;

import com.example.quietconnect_backend.Post.Post;
import com.example.quietconnect_backend.user.Battery;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {
    
    private Long postId;
    private String community_name;
    private String username;
    private String title;
    private String description;
    private Long createdById;
    private LocalTime createdAt;
    private long likeCount;
    private long commentCount;
    private Integer score;

public FeedDto(Post post) {
    this.postId = post.getId();
    this.community_name = post.getCommunity().getCommunityTitle();
    this.username = post.getCreatedBy().getUsername();
    this.title = post.getTitle();
    this.description = post.getDescription();
    this.createdById = post.getCreatedBy().getId();
    this.createdAt = post.getCreatedAt();
    this.likeCount = post.getLikes().size();
    this.commentCount = post.getComments().size();

    // ---- Battery score (null-safe) ----
    if (post.getCreatedBy() != null &&
        post.getCreatedBy().getBattery() != null &&
        post.getCreatedBy().getBattery().getBatteryPercentage() != null) {

        this.score = post.getCreatedBy()
                         .getBattery()
                         .getBatteryPercentage();
    } else {
        this.score = 0;
    }
}

}
