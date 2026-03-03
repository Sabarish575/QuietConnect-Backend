package com.example.quietconnect_backend.dto;

import lombok.Data;

@Data
public class PostStatDto {

    private int noOfPosts;
    private int noOfComments;
    private Long reputationScore;
    
}
