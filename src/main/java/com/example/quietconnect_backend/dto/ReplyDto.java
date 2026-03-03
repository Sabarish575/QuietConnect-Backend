package com.example.quietconnect_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDto {
    public Long id;
    public Long comment_id;
    public String username;
    public String bio;
    public String reply;
}
