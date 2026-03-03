package com.example.quietconnect_backend.Post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/api")
public class PostController {

    private final PostService postService;
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/isMember/{id}")
    public boolean getMethodName(@PathVariable Long id,Authentication authentication) {
        return postService.isMember(id, authentication.getName().toLowerCase());
    }
    


    @GetMapping("/getPosts/{id}")
    public ResponseEntity<SinglePostDto> getSinglePost(@PathVariable Long id) {
        SinglePostDto spd=postService.getSinglePost(id);
        if(spd!=null){
            return ResponseEntity.ok(spd);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/addPosts")
    public Long addPosts(@RequestBody CreatePostDto createPostDto,Authentication authentication) {
        String email=authentication.getName().trim().toLowerCase();
        return postService.createPost(createPostDto, email);
    }
    


    @GetMapping("/getCommunityPosts/{id}")
    public org.springframework.data.domain.Page<PostDto> getCommunityPost(@PathVariable Long id,@PageableDefault(
        size=10,sort = "createdAt",direction = Sort.Direction.DESC
    )Pageable pageable) {

        return postService.getPost(pageable,id);
    }

    @GetMapping("/getUserCreatedPosts")
    public Page<PostDto> getUserCreatedPosts(@PageableDefault(
        size = 10,sort="createdAt",direction = Sort.Direction.DESC
    )Pageable pageable,Authentication authentication) {
        String email=authentication.getName().trim().toLowerCase();
        return postService.getUserCreatedPosts(email, pageable);
    }

    @GetMapping("/getUserCommentedPosts")
    public Page<FrontCommentDto> getUserCommentedPosts(@PageableDefault(
        size = 10,sort="createdAt",direction = Sort.Direction.DESC
    )Pageable pageable,Authentication authentication) {
        String email=authentication.getName().trim().toLowerCase();
        return postService.getUserCommentedPosts(email, pageable);
    }
    
    

    @PostMapping("like/{id}")
    public LikeResponse likeUpdate(@PathVariable Long id,Authentication authentication) {
        String email=authentication.getName().trim().toLowerCase();
        return postService.toggleLike(email, id);
    }
    
    @PatchMapping("editPost/{id}")
    public UpdatePostDto putMethodName(@PathVariable Long id,@RequestBody EditPostDto editPostDto,Authentication authentication) {
        return postService.updatePost(id,editPostDto,authentication.getName());
    }

    @PostMapping("/addComment")
    public ResponseEntity<FrontCommentDto> addComment(@RequestBody CommentDto comment, Authentication authentication){
        if(authentication == null || authentication.getName() == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(null);
        }
        String email = authentication.getName().trim().toLowerCase();
        // Optional: validate comment
        if(comment == null || comment.getComment() == null || comment.getComment().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(null);
        }
        FrontCommentDto response = postService.addComment(comment, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/comments/{id}")
    public Page<FrontCommentDto> getComments(@PathVariable Long id,@PageableDefault(
        size = 10,sort = "createdAt",direction = Sort.Direction.DESC) Pageable pageable){
        return postService.getComments(pageable,id);
    }


    @PostMapping("/addReply")
    public ResponseEntity<String> addReply(@RequestBody ReplyDto reply, Authentication authentication){
        if(authentication == null || authentication.getName() == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body("User not authenticated");
        }
        String email = authentication.getName().trim().toLowerCase();
        // Optional: validate comment
        if(reply == null || reply.getReply() == null || reply.getReply().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body("Comment cannot be empty");
        }
        String response = postService.addReply(reply, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/getReply/{id}")
    public List<FrontReplyDto> getReply(@PathVariable Long id){
        return postService.getReply(id);
    }

    @GetMapping("/userFeed")
    public Page<FeedDto> getMethodName(Authentication authentication,@PageableDefault(
        size = 10, direction = Sort.Direction.DESC, sort = "createdAt"
    )Pageable pageable) {

        String email=authentication.getName().trim().toLowerCase();
        return postService.userFeed(email, pageable);

    }
    
    



    



    







}
