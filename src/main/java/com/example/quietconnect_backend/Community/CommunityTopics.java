package com.example.quietconnect_backend.Community;

import com.example.quietconnect_backend.Topics.Topics;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"community_id", "topics_id"}
    )
)

public class CommunityTopics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "community_id",nullable = false)
    private Communities community;

    @ManyToOne
    @JoinColumn(name="topics_id",nullable = false)
    private Topics topic;

}
