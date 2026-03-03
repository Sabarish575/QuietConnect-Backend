package com.example.quietconnect_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserDto {
    private String username;
    private String bio;
    private Long noOflikes;
    private Long noOfcomments;
    private Long reputation_score;
}
