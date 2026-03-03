package com.example.quietconnect_backend.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class UpdatePostDto {

    private String title;
    private String description;
    private LocalTime editedAt;

    
}
