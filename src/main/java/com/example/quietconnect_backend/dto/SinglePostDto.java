package com.example.quietconnect_backend.dto;

import java.time.LocalTime;
import java.util.List;

import com.example.quietconnect_backend.Post.Comment;
import com.example.quietconnect_backend.Post.Post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SinglePostDto {
    private Long id;
    private String title;
    private String description;
    private Long communityId;
    private UserDto createdBy;
    private LocalTime createdAt;
    private int likeCount;
    private int commentsCount;
    




}
