package com.example.quietconnect_backend.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrontCommentDto {

    private Long id;
    private Long postId;
    private Long userId;
    private String userName;
    private String bio;
    private String comment;
    private LocalTime createdAt;
    
}
