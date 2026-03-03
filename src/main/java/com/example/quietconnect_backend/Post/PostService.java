package com.example.quietconnect_backend.Post;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.quietconnect_backend.Community.Communities;
import com.example.quietconnect_backend.Community.CommunityMemberRepo;
import com.example.quietconnect_backend.Community.CommunityRepository;
import com.example.quietconnect_backend.dto.CommentDto;
import com.example.quietconnect_backend.dto.CreatePostDto;
import com.example.quietconnect_backend.dto.EditPostDto;
import com.example.quietconnect_backend.dto.FeedDto;
import com.example.quietconnect_backend.dto.FrontCommentDto;
import com.example.quietconnect_backend.dto.FrontReplyDto;
import com.example.quietconnect_backend.dto.LikeResponse;
import com.example.quietconnect_backend.dto.PostDto;
import com.example.quietconnect_backend.dto.ReplyDto;
import com.example.quietconnect_backend.dto.SinglePostDto;
import com.example.quietconnect_backend.dto.UpdatePostDto;
import com.example.quietconnect_backend.dto.UserDto;
import com.example.quietconnect_backend.dto.UserJoinedCommunity;
import com.example.quietconnect_backend.user.User;
import com.example.quietconnect_backend.user.UserRepository;
import com.example.quietconnect_backend.wrapper.RestPage;

import jakarta.transaction.Transactional;

import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class PostService {


    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityMemberRepo communityMemberRepo;
    private final LikeRepo likeRepo;
    private final CommentRepo commentRepo;
    private final ReplyRepo replyRepo;

    public PostService(PostRepository postRepository,
        CommunityRepository communityRepository,
        UserRepository userRepository,
        CommunityMemberRepo communityMemberRepo,
        LikeRepo likeRepo,CommentRepo commentRepo,
        ReplyRepo replyRepo) {
        this.postRepository = postRepository;
        this.communityRepository=communityRepository;
        this.userRepository=userRepository;
        this.communityMemberRepo=communityMemberRepo;
        this.likeRepo=likeRepo;
        this.commentRepo=commentRepo;
        this.replyRepo=replyRepo;
    }

    public boolean isMember(Long comId,String email){

        System.out.println("your com id and email"+comId+" "+email);
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        Communities com=communityRepository.findById(comId).orElseThrow(()->new RuntimeException("Community not found"));

       return communityMemberRepo.existsByCommunitiesIdAndUserId(com.getId(), user.getId());
    }

    //Creating the post
    @Caching(evict = {
        @CacheEvict(value = "postStatDto",key = "#email.toLowerCase()"),
        @CacheEvict(value = "getCommunityPosts",allEntries = true),
        @CacheEvict(value = "userCreatedPosts",allEntries = true),
        @CacheEvict(value = "userFeed",allEntries = true)
    })
    public Long createPost(CreatePostDto post,String email){
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        Communities com=communityRepository.findById(post.getCommunityId()).orElseThrow(()->new RuntimeException("Community not found"));

        boolean isMember=isMember(com.getId(), email);

        if(!isMember){
            throw new RuntimeException("User not in the respective community");
        }

        Post p1=new Post();
        p1.setCommunity(com);
        p1.setTitle(post.getTitle());
        p1.setDescription(post.getDescription());
        p1.setCreatedBy(user);
        return postRepository.save(p1).getCommunity().getId();

    }

    //deleting the post
    @Caching(evict={
        @CacheEvict(value = "getSinglePost",key ="#postId" ),
        @CacheEvict(value = "getCommunityPosts",allEntries = true),
        @CacheEvict(value = "userCreatedPosts", allEntries = true),
        @CacheEvict(value = "userFeed", allEntries = true),
        @CacheEvict(value = "postStatDto", key = "#email.toLowerCase()") 
    })
    public void deletePost(Long postId,String email){
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        Post post=postRepository.findById(postId).orElseThrow(()->new RuntimeException("posts not found"));
        if(!post.getCreatedBy().getId().equals(user.getId())){
            throw new RuntimeException("Unauthorized");
        }
        postRepository.delete(post);
    }

    //update the post
    @Transactional

    @Caching(evict = {
        @CacheEvict(value = "getSinglePost",key = "#postId"),
        @CacheEvict(value = "getCommunityPosts",allEntries = true),
        @CacheEvict(value = "userCreatedPosts",allEntries = true),
        @CacheEvict(value = "userFeed", allEntries = true),

    })
public UpdatePostDto updatePost(Long postId, EditPostDto editPostDto, String email) {

    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // Correct validation
    if (!post.getCreatedBy().getId().equals(user.getId())) {
        throw new RuntimeException("Unauthorized");
    }

    post.setTitle(editPostDto.getTitle());
    post.setDescription(editPostDto.getDescription());
    post.setCreatedAt(LocalTime.now());

    postRepository.save(post);

    UpdatePostDto upd = new UpdatePostDto();
    upd.setTitle(post.getTitle());
    upd.setDescription(post.getDescription());
    upd.setEditedAt(post.getCreatedAt());
    return upd;
}

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "getSinglePost", key = "#postId"),
        @CacheEvict(value = "getCommunityPosts", allEntries = true),
        @CacheEvict(value = "userFeed", allEntries = true),    
    })
    public LikeResponse toggleLike(String email,Long postId){
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        Post post=postRepository.findById(postId).orElseThrow(()->new RuntimeException("posts not found"));

        boolean liked;

        if(likeRepo.existsByPostIdAndUserId(postId, user.getId())){
            unlikeButton(user, post);
            liked=false;

        }
        else{
            likeButton(user, post);
            liked=true;
        }

        return new LikeResponse(likeRepo.countByPostId(postId),liked);


    }

    //liked post
    public void likeButton(User user,Post post){
        Like like=new Like();
        like.setPost(post);
        like.setUser(user);
        likeRepo.save(like);
    }

    //unliked post
    public void unlikeButton(User user, Post post) {
        Like like = likeRepo.findByPostIdAndUserId(post.getId(), user.getId());
        if (like != null) {
        likeRepo.delete(like);
        }
    }

    //Returned the page object with post content
    //it has hasNext(),getContent(),totalPages() for frontend use

    @Cacheable(value = "getCommunityPosts",key ="#communityId+ ':' + #pageable.pageNumber + ':' + #pageable.pageSize" )
    public RestPage<PostDto> getPost(Pageable pageable, Long communityId) {

        System.out.println("db hit from getpost for community related posts");
        
        Communities com = communityRepository.findById(communityId)
            .orElseThrow(() -> new RuntimeException("Community not found"));

        Page<Post> postsPage = postRepository.findByCommunity(com, pageable);

        // Map each Post to PostDto
        List<PostDto> dtoList = postsPage.getContent()
            .stream().map(post->new PostDto(post))
            .collect(Collectors.toList());

        // Return Page<PostDto> to frontend (preserve paging info)
        return new RestPage<>(dtoList, pageable.getPageNumber(),pageable.getPageSize(), postsPage.getTotalElements());
    }

    @Cacheable(value = "userCreatedPosts",key = "#email.toLowerCase()+ ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public RestPage<PostDto> getUserCreatedPosts(String email,Pageable pageable){
        System.out.println("--------->db hit");
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        Page<Post> userPosts=postRepository.findByCreatedById(user.getId(), pageable);
        List<PostDto> dtoList=userPosts.getContent()
                .stream()
                .map(p->new PostDto(p)).collect(Collectors.toList());
        return new RestPage<>(dtoList, pageable.getPageNumber(),pageable.getPageSize(), userPosts.getTotalElements());
    }

    @Cacheable(value = "userCommentedPosts",key = "#email.toLowerCase()+ ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public RestPage<FrontCommentDto> getUserCommentedPosts(String email,Pageable pageable){
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        Page<FrontCommentDto> response=commentRepo.findByUserId(user.getId(), pageable);

        return new RestPage<>(response.getContent(),pageable.getPageNumber(),pageable.getPageSize(),response.getTotalElements());
    }


    @Cacheable(value="getSinglePost",key="#postId")
    public SinglePostDto getSinglePost(Long postId){

        Post post=postRepository.findById(postId).orElseThrow(()->new RuntimeException("Post not found"));

        SinglePostDto spd=new SinglePostDto();

        UserDto usd=new UserDto();

        usd.setUserId(post.getCreatedBy().getId());
        usd.setName(post.getCreatedBy().getUsername());
        usd.setBio(post.getCreatedBy().getBio());

        spd.setId(postId);
        spd.setTitle(post.getTitle());
        spd.setDescription(post.getDescription());
        spd.setCreatedAt(post.getCreatedAt());
        spd.setCreatedBy(usd);
        spd.setCommentsCount(post.getComments().size());
        spd.setCommunityId(post.getCommunity().getId());
        spd.setLikeCount(post.getLikes().size());
        return spd;
    }

    //add Comment
    @Caching(evict = {
        @CacheEvict(value = "getSinglePost", key = "#comment.post_id"),
        @CacheEvict(value = "getCommunityPosts", allEntries = true),
        @CacheEvict(value = "userFeed", allEntries = true),
        @CacheEvict(value = "userCommentedPosts", allEntries = true),
        @CacheEvict(value = "getComments", allEntries = true),
        @CacheEvict(value = "userFeed", allEntries = true)
    })
    public FrontCommentDto addComment(CommentDto comment,String email){
        Post post=postRepository.findById(comment.getPost_id()).orElseThrow(()->new RuntimeException("Post not found"));
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
       
        
        Comment com1=new Comment();
        com1.setPost(post);
        com1.setUser(user);
        com1.setComment(comment.getComment());
        commentRepo.save(com1);


        FrontCommentDto fcd=new FrontCommentDto();

        fcd.setId(com1.getId());
        fcd.setUserName(com1.getUser().getUsername());
        fcd.setComment(com1.getComment());
        fcd.setCreatedAt(com1.getCreatedAt());
        fcd.setBio(com1.getUser().getBio());

        return fcd;
    }

    @Transactional
    @Cacheable(value = "getComments",key ="#postId+ ':' + #pageable.pageNumber + ':' + #pageable.pageSize" )
    public RestPage<FrontCommentDto> getComments(Pageable pageable,Long postId){
        Post post=postRepository.findById(postId).orElseThrow(()->new RuntimeException("Post not found"));

        Page<FrontCommentDto> res=commentRepo.findByPost(post, pageable);

        return new RestPage<>(res.getContent(),pageable.getPageNumber(),pageable.getPageSize(),res.getTotalElements());
        
    }
    @Caching(evict = {
        @CacheEvict(value = "getReply", key = "#reply.comment_id"),
        @CacheEvict(value = "getComments", allEntries = true) 
    })
    public String addReply(ReplyDto reply,String email){
        Comment comment=commentRepo.findById(reply.getComment_id()).orElseThrow(()->new RuntimeException("Comment not found"));
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        Reply reply2=new Reply();
        reply2.setReply(reply.getReply());
        reply2.setComment(comment);
        reply2.setUser(user);
        replyRepo.save(reply2);
        return "You Replied!";
    }

    @Cacheable(value = "getReply",key = "#commentId")
    public List<FrontReplyDto> getReply(Long commentId){
        Comment comment=commentRepo.findById(commentId).orElseThrow(()->new RuntimeException("Comment not found"));
        return replyRepo.findByComment(comment);
    }


    @Cacheable(value = "userFeed",key = "#email.toLowerCase()+ ':' + #pageable.pageNumber + ':' + #pageable.pageSize")
    public RestPage<FeedDto> userFeed(String email,Pageable pageable){
        System.out.println("---------->DB HIT");
        User user=userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        boolean hasJoinedCommunity=communityMemberRepo.existsByUserId(user.getId());
        Page<Post> posts;
        if(hasJoinedCommunity){
            posts=postRepository.findUserFeed(user.getId(), pageable);
        }
        else{
            posts=postRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<FeedDto> dtoList=posts.getContent().stream().map(FeedDto::new).collect(Collectors.toList());
        return new RestPage<>(dtoList,pageable.getPageNumber(),pageable.getPageSize(),posts.getTotalElements());
    }

}
