package com.example.quietconnect_backend.Community;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import com.example.quietconnect_backend.Topics.TopicRepository;
import com.example.quietconnect_backend.Topics.Topics;
import com.example.quietconnect_backend.dto.CommunityDetail;
import com.example.quietconnect_backend.dto.CommunityDetails;
import com.example.quietconnect_backend.dto.UserCreatedCommunity;
import com.example.quietconnect_backend.dto.UserJoinedCommunity;
import com.example.quietconnect_backend.user.User;
import com.example.quietconnect_backend.user.UserRepository;
import com.example.quietconnect_backend.wrapper.RestPage;

import org.springframework.data.domain.*;


import jakarta.transaction.Transactional;

@Service
public class CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private CommunityTopicsRepo communityTopicsRepo;


    @Autowired
    private CommunityMemberRepo communityMemberRepo;

    boolean isExist(String name){
        return communityRepository.existsByCommunityTitle(name);
    }


    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "getAllCommunities",allEntries = true),
        @CacheEvict(value = "popularCommunites",allEntries = true)
    })

    public Long addCommunityDetails(CommunityDetail entity,String email){

        if(isExist(entity.getCommunityTitle())){
            return null;
        }

        // creation of community occurs
        Communities com=new Communities();
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        com.setCommunityTitle(entity.getCommunityTitle());
        com.setCommunityBio(entity.getCommunityBio());
        com.setCreatedBy(user);
        communityRepository.save(com);

        //auto joining the creator as a member in the community
        CommunityMember comMem=new CommunityMember();
        comMem.setCommunities(com);
        comMem.setUser(user);
        communityMemberRepo.save(comMem);

        //traversing through the topic arrays and setting the ids
        if (entity.getTopicIds() != null && !entity.getTopicIds().isEmpty()) {
            // Fetch all topics in a single query
            List<Topics> topics = topicRepository.findAllById(entity.getTopicIds());

            // Check if all requested IDs exist
            if (topics.size() != entity.getTopicIds().size()) {
                throw new RuntimeException("Some topics not found");
            }

            // Map each topic to a CommunityTopics object
            List<CommunityTopics> topicList = topics.stream().map(topic -> {
                CommunityTopics comTop = new CommunityTopics();
                comTop.setCommunity(com);
                comTop.setTopic(topic);
                return comTop;
            }).toList();

            // Save all community topics at once
            communityTopicsRepo.saveAll(topicList);
        }

        return com.getId();
    }


    @Cacheable(value = "getCommunityDetails",key = "#id")
    public CommunityDetails getDetails(Long id){

        //got all the community details
        Communities com=communityRepository.findById(id).orElseThrow(()->new RuntimeException("Communities not found"));

        //got all the count of members
        Long count=communityMemberRepo.countByCommunitiesId(id);

        List<String> topicName=communityTopicsRepo.findTopicNamesByCommunityId(id);

        CommunityDetails comD=new CommunityDetails();

        comD.setCommunityBio(com.getCommunityBio());
        comD.setCommunityTitle(com.getCommunityTitle());
        comD.setMembers(count);
        comD.setTopicIds(topicName);

        return comD;
    }

    @Caching(evict = {
        @CacheEvict(value = "getAllCommunities",allEntries = true),
        @CacheEvict(value = "popularCommunites",allEntries = true),
        @CacheEvict(value = "userCommentedPosts", allEntries = true)
    })
    //for deleting the community only By creator
    public void deleteCommunity(Long id,String email) {
        Communities com = communityRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Community not found"));

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!com.getCreatedBy().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized: Only creator can delete");
        }
        communityRepository.delete(com);
    }


    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "userJoinedCommunity", key = "#email.toLowerCase()"),
        @CacheEvict(value = "getCommunityDetails", key = "#communityId"),
        @CacheEvict(value = "getAllCommunities", allEntries = true),
        @CacheEvict(value = "popularCommunites", allEntries = true),
        @CacheEvict(value = "searchCommunity", allEntries = true),
        @CacheEvict(value = "searchPopularCommunity", allEntries = true)
    })
    public String toggleFollow(Long communityId, String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (communityMemberRepo.existsByCommunitiesIdAndUserId(communityId, user.getId())) {
            return unfollowCommunity(communityId, user);
        }
        return followCommunity(communityId, user);
    }

    //unfollow
    public String unfollowCommunity(Long communityId, User user) {
        CommunityMember comMem = communityMemberRepo
            .findByCommunitiesIdAndUserId(communityId, user.getId())
            .orElseThrow(() -> new RuntimeException("User is not following"));
        communityMemberRepo.delete(comMem);
        return "unfollowed";
    }

    //follow
    public String followCommunity(Long communityId, User user) {
        Communities com = communityRepository.findById(communityId)
            .orElseThrow(() -> new RuntimeException("Community not found"));
        CommunityMember comMem = new CommunityMember();
        comMem.setCommunities(com);
        comMem.setUser(user);
        communityMemberRepo.save(comMem);
        return "followed";
    }


    //return the user created community
    @Cacheable(value = "userCreatedCommunity",key = "#email.toLowerCase()")
    public List<UserCreatedCommunity> findCreatedCommunity(String email){
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return communityRepository.findByCreatedById(user.getId());
    }

    //return the user joined community
    @Cacheable(value = "userJoinedCommunity",key = "#email.toLowerCase()")
    public List<UserJoinedCommunity> findJoinedCommunities(String email){
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        return communityMemberRepo.findJoinedByUserId(user.getId());
    }

    @Cacheable(value = "searchCommunity",key = "#value")
    public List<CommunityDetails> searchCommunityDetails(String value){

        return communityRepository.findByWord(value)
                .stream()
                .map(c->new CommunityDetails(
                    c.getId(),
                    c.getCommunityTitle(),
                    c.getCommunityBio(),
                    c.getCommunityTopics()
                        .stream()
                        .map(CommunityTopics::getTopic)
                        .map(Topics::getName)
                        .collect(Collectors.toList()),
                    (long) c.getCommunityMember().size()                    
                ))
                .collect(Collectors.toList());
    }


    @Cacheable(value = "searchPopularCommunity",key = "#value")
    public List<CommunityDetails> searchPopularCommunityDetails(String value){
                return communityRepository.findByWordByPopular(value)
                .stream()
                .map(c->new CommunityDetails(
                    c.getId(),
                    c.getCommunityTitle(),
                    c.getCommunityBio(),
                    c.getCommunityTopics()
                        .stream()
                        .map(CommunityTopics::getTopic)
                        .map(Topics::getName)
                        .collect(Collectors.toList()),
                    (long) c.getCommunityMember().size()                    
                ))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "getAllCommunities",key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public RestPage<CommunityDetails> getAllCommunities(org.springframework.data.domain.Pageable pageable) {

    Page<Communities> page = communityRepository.findAll(pageable);

    List<CommunityDetails> res = page.getContent()
            .stream()
            .map(c -> new CommunityDetails(
                    c.getId(),
                    c.getCommunityTitle(),
                    c.getCommunityBio(),
                    c.getCommunityTopics()
                            .stream()
                            .map(CommunityTopics::getTopic)
                            .map(Topics::getName)
                            .toList(),
                    (long) c.getCommunityMember().size()
            ))
            .toList();
    return new RestPage<>(res, pageable.getPageNumber(),pageable.getPageSize(), page.getTotalElements());
    }   

    @Cacheable(value = "popularCommunites",key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public RestPage<CommunityDetails> getPopularCommunities(org.springframework.data.domain.Pageable pageable){

        Page<Communities> page = communityRepository.findByPopularity(pageable);
         List<CommunityDetails> res= page.getContent().stream()
                .map(c->new CommunityDetails(
                    c.getId(),
                    c.getCommunityTitle(),
                    c.getCommunityBio(),
                    c.getCommunityTopics()
                        .stream()
                        .map(CommunityTopics::getTopic)
                        .map(Topics::getName)
                        .toList(),
                    (long) c.getCommunityMember().size()
                )).toList();
        
        return new RestPage<>(res, pageable.getPageNumber(),pageable.getPageSize(), page.getTotalElements());

    }

    public boolean checkJoined(Long communityId,String email){

        Communities com = communityRepository.findById(communityId)
            .orElseThrow(() -> new RuntimeException("Community not found"));

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (communityMemberRepo.existsByCommunitiesIdAndUserId(communityId, user.getId())) {
            return true;
        }
        return false;
    }


}
