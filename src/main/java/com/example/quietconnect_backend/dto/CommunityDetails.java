package com.example.quietconnect_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityDetails {
    private Long id;
    private String communityTitle;
    private String communityBio;
    private List<String> topicIds;
    private Long members;
}
