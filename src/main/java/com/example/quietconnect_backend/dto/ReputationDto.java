package com.example.quietconnect_backend.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReputationDto {

    private Long userId;
    private LocalDate snapShotDate;
    private Long score;
    
}
