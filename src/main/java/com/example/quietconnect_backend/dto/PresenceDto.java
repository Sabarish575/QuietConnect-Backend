package com.example.quietconnect_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PresenceDto {
    private String userId;
    private boolean online;
    public PresenceDto(String id,boolean online){
        this.userId=id;
        this.online=online;
    } 
}
