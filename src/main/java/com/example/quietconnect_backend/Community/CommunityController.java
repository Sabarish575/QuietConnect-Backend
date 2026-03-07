package com.example.quietconnect_backend.Community;

import java.util.Collections;
import java.util.List;

import org.hibernate.query.SortDirection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quietconnect_backend.Exception.CommunityNotFoundException;
import com.example.quietconnect_backend.Exception.UnauthorizedException;
import com.example.quietconnect_backend.dto.CommunityDetail;
import com.example.quietconnect_backend.dto.CommunityDetails;
import com.example.quietconnect_backend.dto.UserCreatedCommunity;
import com.example.quietconnect_backend.dto.UserJoinedCommunity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/community")
public class CommunityController {



    @Autowired
    private CommunityService service;


    @PostMapping("/create")
    public ResponseEntity<Long> createCommunity(@RequestBody CommunityDetail entity, Authentication authentication) {
        String email = authentication.getName().trim().toLowerCase();
        Long com_id = service.addCommunityDetails(entity, email);

        if (com_id == null) {
            // Return 400 Bad Request if title already exists
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(com_id);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CommunityDetails> communityDetails(@PathVariable Long id) {
        System.out.println("id "+id);
        CommunityDetails comD=service.getDetails(id);
        if(comD!=null){
            return ResponseEntity.ok(comD);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> dltCommunity(@PathVariable Long id,Authentication authentication){

        String email=authentication.getName().trim().toLowerCase();
        try {
            service.deleteCommunity(id, email);
            return ResponseEntity.ok("Community deleted");
        } catch (CommunityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Community not found");
        }
        catch(UnauthorizedException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized one");
        }
    }

    //get the created community details
    @GetMapping("/createdCom")
    public ResponseEntity<List<UserCreatedCommunity>> userCreated(Authentication authentication) {

       String email=authentication.getName().trim().toLowerCase();

       try {
        List<UserCreatedCommunity> data=service.findCreatedCommunity(email);
        return ResponseEntity.ok(data);
       } catch (Exception e) {
        return ResponseEntity.ok(Collections.emptyList());
       }
    }
    

    //get the joined community details
    @GetMapping("/joinedCom")
    public ResponseEntity<List<UserJoinedCommunity>> userJoined(Authentication authentication) {
        String email=authentication.getName().trim().toLowerCase();

        try{
            List<UserJoinedCommunity> data=service.findJoinedCommunities(email);
            return ResponseEntity.ok(data);
        }
        catch(Exception e){
            return ResponseEntity.ok(Collections.emptyList());
        }
    }


    @GetMapping("/nameExist")
    public String getMethodName(@RequestBody String name) {
        return new String();
    }

    @GetMapping("/search/{value}")
    public List<CommunityDetails> searchCommunities(@PathVariable String value){
        if(!value.trim().equals("")){
            return service.searchCommunityDetails(value);
        }
        return null;
    }

    @GetMapping("/searchPop/{value}")
    public List<CommunityDetails> serachPopByword(@PathVariable String value) {
        if(!value.trim().equals("")){
            return service.searchPopularCommunityDetails(value);
        }
        return null;
    }
    

    @GetMapping("/getAll")
    public Page<CommunityDetails> getAll(@PageableDefault(
        size = 10
    )Pageable pageable) {
        return service.getAllCommunities(pageable);
    }

    @GetMapping("/getPop")
    public Page<CommunityDetails> getPop(@PageableDefault(
        size = 10
    )Pageable pageable){
        return service.getPopularCommunities(pageable);
    }


    @PostMapping("/joinandunjoin/{id}")
    public ResponseEntity<String> togglefollow(@PathVariable Long id,Authentication authentication) {

        String email=authentication.getName().trim().toLowerCase();
        String response=service.toggleFollow(id, email);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{id}")
    public boolean check(@PathVariable Long id,Authentication authentication) {
        String email=authentication.getName().trim().toLowerCase();
        return service.checkJoined(id, email);
    }
    
    
    
    

    
    
    
}
