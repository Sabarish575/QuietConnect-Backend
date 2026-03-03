package com.example.quietconnect_backend.User_Reputation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quietconnect_backend.dto.ReputationDto;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/score")
public class ReputationController {

    private ReputationService reputationService;


    public ReputationController(ReputationService reputationService) {
        this.reputationService = reputationService;
    }


    @GetMapping("/getReputation/{id}")
    public List<ReputationDto> getMethodName(@PathVariable Long id) {
        return reputationService.getScore(id);
    }
    
    
}
