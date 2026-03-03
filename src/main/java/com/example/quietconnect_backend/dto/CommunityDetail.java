package com.example.quietconnect_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityDetail {
    
    private String communityTitle;
    private String communityBio;
    private List<Long> topicIds;

}
