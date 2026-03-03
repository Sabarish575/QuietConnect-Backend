package com.example.quietconnect_backend.Community;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.quietconnect_backend.Topics.Topics;

@Repository
public interface CommunityTopicsRepo extends JpaRepository<CommunityTopics,Long> {
    
    @Query("""
        SELECT t.name FROM CommunityTopics ct JOIN ct.topic t WHERE ct.community.id = :id
    """)
    List<String> findTopicNamesByCommunityId(Long id);

    

}
