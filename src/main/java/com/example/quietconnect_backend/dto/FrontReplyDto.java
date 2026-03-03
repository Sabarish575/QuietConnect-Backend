package com.example.quietconnect_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class FrontReplyDto {
    private Long id;
    private Long commentId;
    private String userName;
    private String bio;
    private String reply;
}
