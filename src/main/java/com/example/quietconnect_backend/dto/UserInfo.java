package com.example.quietconnect_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserInfo {

    private String username;
    private String bio;
    private LocalDate createdAt;
    private String email;
    private int communityJoined;
    private int postsCreated;
    private Integer score;
    private Long reputation_score;

}
