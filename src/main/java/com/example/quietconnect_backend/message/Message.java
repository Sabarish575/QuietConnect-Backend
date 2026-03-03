package com.example.quietconnect_backend.message;


import java.time.LocalDateTime;
import java.time.LocalTime;


import com.example.quietconnect_backend.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Data;


@Entity
@Data

public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    private LocalDateTime createdAt;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User receiver;



    @PrePersist
    public void save(){
        this.createdAt=LocalDateTime.now();
    }


}
