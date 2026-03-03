package com.example.quietconnect_backend.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatedCommunity {

    private Long communityId;
    private String communityTitle;   // renamed
    private String communityBio;    // renamed
    private Long communityCount;    // renamed
    private LocalDate createdAt;
}
