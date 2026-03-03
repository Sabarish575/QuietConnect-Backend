package com.example.quietconnect_backend.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinedCommunity {

    private Long communityId;
    private String communityTitle;
    private String communityBio;
    private LocalDate joinedAt;
}
