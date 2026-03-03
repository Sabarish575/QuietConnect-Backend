package com.example.quietconnect_backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatContentDto {

    private Long senderId;
    private Long receiverId;
    private String message;
    private LocalDateTime createdAt;
    
}
