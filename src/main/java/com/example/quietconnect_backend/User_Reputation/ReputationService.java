package com.example.quietconnect_backend.User_Reputation;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.example.quietconnect_backend.Post.CommentRepo;
import com.example.quietconnect_backend.Post.LikeRepo;
import com.example.quietconnect_backend.dto.ReputationDto;
import com.example.quietconnect_backend.user.User;
import com.example.quietconnect_backend.user.UserRepository;

@Service
public class ReputationService {
    private ReputationRepo reputationRepo;
    private UserRepository userRepository;
    private LikeRepo likeRepo;
    private CommentRepo commentRepo;

    public ReputationService(ReputationRepo reputationRepo, UserRepository userRepository, LikeRepo likeRepo,
            CommentRepo commentRepo) {
        this.reputationRepo = reputationRepo;
        this.userRepository = userRepository;
        this.likeRepo = likeRepo;
        this.commentRepo = commentRepo;
    }

    @CacheEvict(value = {"getReputation","postStatDto"},allEntries = true)
    public Long CalculateRepo(){

        List<User> users=userRepository.findAll();

        LocalDate today=LocalDate.now();
        Long score=0L;

        for(User u:users){


            boolean exists=reputationRepo.existsByUserAndSnapShotDate(u, today);

            if(!exists){
                Long id=u.getId();
                Long post_likes=likeRepo.countByUserId(id);
                Long comment=commentRepo.countByUserId(id);

                score=post_likes+comment;

                Reputation rp=new Reputation();
                rp.setUser(u);
                rp.setSnapShotDate(today);
                rp.setReputation_score(post_likes+comment);
                reputationRepo.save(rp);
            }
        }

        return score;
    }

    @Cacheable(value = "getReputation",key = "#userId")
    public List<ReputationDto> getScore(Long userId){
        return reputationRepo.findByUserId(userId);
    }

}
