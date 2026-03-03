package com.example.quietconnect_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostDto {

    private String title;
    private String description;
    private Long communityId;
    

}
