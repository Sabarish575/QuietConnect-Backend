package com.example.quietconnect_backend.User_Reputation;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ReputationSchedular {

    private ReputationService reputationService;

    public ReputationSchedular(ReputationService reputationService){
        this.reputationService=reputationService;
    }

    @Scheduled(cron = "0 */15 * * * ?")
    public void getUserdata(){
        reputationService.CalculateRepo();
    }
    
}
