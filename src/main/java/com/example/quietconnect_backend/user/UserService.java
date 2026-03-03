package com.example.quietconnect_backend.user;

import java.util.Optional;



import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;

import org.springframework.cache.annotation.Cacheable;

import org.springframework.cache.annotation.Caching;

import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.stereotype.Service;



import com.example.quietconnect_backend.Post.CommentRepo;

import com.example.quietconnect_backend.Post.LikeRepo;

import com.example.quietconnect_backend.Post.PostRepository;

import com.example.quietconnect_backend.User_Reputation.ReputationRepo;

import com.example.quietconnect_backend.dao.BatteryDaoImpl;

import com.example.quietconnect_backend.dto.BatteryDto;

import com.example.quietconnect_backend.dto.PostStatDto;

import com.example.quietconnect_backend.dto.UpdateProfile;

import com.example.quietconnect_backend.dto.UserCardDto;

import com.example.quietconnect_backend.dto.UserDto;

import com.example.quietconnect_backend.dto.UserInfo;



import jakarta.transaction.Transactional;


@Service
public class UserService {

    private final UserRepository repo;
    private final PostRepository postrepo;
    private final CommentRepo commentrepo;
    private final LikeRepo likeRepo;
    private final FollowUserRepo followUserRepo;
    private final BatteryRepo batteryRepo;
    private final ReputationRepo reputationRepo;

    @Autowired
    private final BatteryDaoImpl batteryDaoImpl;

    public UserService(UserRepository repo, PostRepository postRepository, CommentRepo commentRepo, LikeRepo likeRepo, FollowUserRepo followUserRepo, BatteryRepo batteryRepo, ReputationRepo reputationRepo, BatteryDaoImpl batteryDaoImpl) {
        this.repo = repo;
        this.postrepo = postRepository;
        this.commentrepo = commentRepo;
        this.likeRepo = likeRepo;
        this.followUserRepo = followUserRepo;
        this.batteryRepo = batteryRepo;
        this.reputationRepo = reputationRepo;
        this.batteryDaoImpl = batteryDaoImpl;
    }

    @Cacheable(value = "me", key = "#email.toLowerCase()")
    public Number findMe(String email) {
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        return user.getId();
    }

    public User find(String email) {
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        return user;
    }

    @Cacheable(value = "profile", key = "#email.toLowerCase()")
    @Caching(evict = {
        @CacheEvict(value = "me", key = "#email.toLowerCase()")
    })
    public UpdateProfile getUpdateProfile(String email) {
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        UpdateProfile up = new UpdateProfile();
        up.setUserId(user.getId());
        up.setUsername(user.getUsername());
        up.setBio(user.getBio());
        return up;
    }

    @Caching(evict = {
        @CacheEvict(value = "profile", key = "#email.toLowerCase()"),
        @CacheEvict(value = "userInfo", key = "#email.toLowerCase()"),
        @CacheEvict(value = "userCardDto", key = "#email.toLowerCase()"),
        @CacheEvict(value = "me", key = "#email.toLowerCase()"),
        @CacheEvict(value = "userFeed", allEntries = true)
    })
    public void changeUser(String email, UpdateProfile entity) {
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        user.setUsername(entity.getUsername());
        user.setBio(entity.getBio());
        repo.save(user);
    }

    @Cacheable(value = "userInfo", key = "#email.toLowerCase()")
    public UserInfo getInfo(String email) {
        UserInfo ui = new UserInfo();
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        ui.setEmail(user.getEmail());
        ui.setUsername(user.getUsername());
        ui.setCreatedAt(user.getCreatedAt());
        ui.setBio(user.getBio());
        ui.setPostsCreated(user.getPosts().size());
        ui.setCommunityJoined(user.getCommunities().size());
        Long score = reputationRepo.findLatestScoreByUserId(user.getId());
        ui.setReputation_score(score);
        ui.setScore(getScore(email));
        return ui;
    }

    // RESTORED: OAuth2 Logic
    public User processOauth2User(OAuth2User oAuth2User) {
        String emailId = oAuth2User.getAttribute("email");
        String email = emailId.trim().toLowerCase();
        String googleId = oAuth2User.getAttribute("sub");

        return repo.findByEmail(email).orElseGet(() -> {
            User u = new User();
            u.setEmail(email);
            u.setGoogle_id(googleId);
            return repo.save(u);
        });
    }

    @Caching(evict = {
        @CacheEvict(value = "userInfo", key = "#email.toLowerCase()"),
        @CacheEvict(value = "userCardDto", key = "#email.toLowerCase()"),
        @CacheEvict(value = "userFeed", allEntries = true)
    })
    public void userNameExist(String email, String username, String bio) {
        email = email.trim().toLowerCase();
        if (repo.existsByUsername(username)) {
            throw new RuntimeException("Username already taken");
        }
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        user.setUsername(username);
        user.setBio(bio);
        repo.save(user);
    }

    @Cacheable(value = "userDto", key = "#id")
    public UserDto finduser(Long id) {
        return repo.findById(id).map(
                u -> new UserDto(u.getId(), u.getUsername(), u.getBio())).orElseThrow(() -> new RuntimeException("user not found"));
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "userInfo", key = "#email.toLowerCase()"),
        @CacheEvict(value = "userCardDto", key = "#email.toLowerCase()")
    })
    public String toggleFollow(String email, Long followingId) {
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getId().equals(followingId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }
        User following = repo.findById(followingId).orElseThrow(() -> new RuntimeException("User not found"));
        if (followUserRepo.existsByFollowerAndFollowing(user, following)) {
            return unfollow(user, following);
        }
        return follow(user, following);
    }

    public String follow(User user, User following) {
        FollowUser fu = new FollowUser();
        fu.setFollower(user);
        fu.setFollowing(following);
        followUserRepo.save(fu);
        return "Following";
    }

    public String unfollow(User user, User following) {
        followUserRepo.deleteByFollowerAndFollowing(user, following);
        return "Unfollowed successfully";
    }

    @Caching(evict = {
        @CacheEvict(value = "userInfo", key = "#email.toLowerCase()"),
        @CacheEvict(value = "userCardDto", key = "#email.toLowerCase()"),
        @CacheEvict(value = "userFeed", allEntries = true)
    })
    public Integer addBattery(String email, Integer score) {
        System.out.println("------>DB HIT FROM ADDbATTERY");
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Integer updated = batteryRepo.updateBattery(user, score);
        if (updated == 0) {
            Battery bat = new Battery();
            bat.setBatteryPercentage(score);
            bat.setUser(user);
            batteryRepo.save(bat);
        }
        batteryDaoImpl.addByid(user.getId(), score);
        return score;
    }

    public Integer getScore(String email) {
        System.out.println("------>DB HIT FROM GET_SCORE");
        Long userId=((Number) findMe(email)).longValue();
        Integer cachedScore = batteryDaoImpl.getByid(userId);
        if (cachedScore != null) {
            System.out.println("BUT CACHED SCORE");
            return cachedScore;
        }
        Integer score = batteryRepo.findByUserId(userId);
        if (score == null) {
            return 0;
        }
        batteryDaoImpl.addByid(userId, score);
        return score; // FIXED: Returns the score instead of 0
    }

    @Cacheable(value = "userCardDto", key = "#email.toLowerCase()")
    public UserCardDto getUserCardDto(String email) {
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        UserCardDto ucd = new UserCardDto();
        Integer score = getScore(email);
        ucd.setUsername(user.getUsername());
        ucd.setBio(user.getBio());
        ucd.setScore(score);
        return ucd;
    }

    @Cacheable(value = "postStatDto", key = "#email.toLowerCase()")
    public PostStatDto getPostStatDto(String email) {
        User user = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        PostStatDto psd = new PostStatDto();
        psd.setNoOfComments(user.getComments().size());
        psd.setNoOfPosts(user.getPosts().size());
        Long score = reputationRepo.findLatestScoreByUserId(user.getId());
        psd.setReputationScore(score);
        return psd;
    }
}