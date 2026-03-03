package com.example.quietconnect_backend.Topics;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topics,Long> {
    
    List<Topics> findByisActiveTrue();

}
