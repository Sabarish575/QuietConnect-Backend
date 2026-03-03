package com.example.quietconnect_backend.Topics;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/api/topics")

public class TopicController {

    @Autowired
    private TopicService topicService;

    @GetMapping
    public List<Topics> getMethodName(){
       return topicService.getAllTopics();
    }
    
    
}
