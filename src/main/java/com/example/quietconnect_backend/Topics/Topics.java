package com.example.quietconnect_backend.Topics;

import java.util.List;

import com.example.quietconnect_backend.Community.CommunityTopics;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Topics")
public class Topics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topic_id;
    private String name;
    private String category;
    private boolean isActive;

    @OneToMany(mappedBy = "topic",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<CommunityTopics> topics;
    
}
