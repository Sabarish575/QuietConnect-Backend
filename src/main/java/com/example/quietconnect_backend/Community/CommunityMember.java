package com.example.quietconnect_backend.Community;

import java.time.LocalDate;

import org.hibernate.annotations.ManyToAny;

import com.example.quietconnect_backend.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"community_id", "user_id"}
    )
)

public class CommunityMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_id",nullable = false)
    private Communities communities;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    private LocalDate joinedAt;

    @PrePersist
    public void update(){
        this.joinedAt=LocalDate.now();
    }
    


}
