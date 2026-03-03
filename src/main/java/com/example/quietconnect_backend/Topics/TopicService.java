package com.example.quietconnect_backend.Topics;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

    @Autowired
    private TopicRepository topicRepository;


    public List<Topics> getAllTopics(){

        return topicRepository.findByisActiveTrue();
        
    }

}
