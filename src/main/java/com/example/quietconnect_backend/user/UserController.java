package com.example.quietconnect_backend.user;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quietconnect_backend.dto.PostStatDto;
import com.example.quietconnect_backend.dto.UpdateProfile;
import com.example.quietconnect_backend.dto.UserCardDto;
import com.example.quietconnect_backend.dto.UserDto;
import com.example.quietconnect_backend.dto.UserInfo;
import com.example.quietconnect_backend.dto.UsernameRequest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;





@RestController
@RequestMapping("/api/user")
public class UserController {


    private final UserService userService;

    @GetMapping("/username")
    public UpdateProfile getName(Authentication authentication) {
        return userService.getUpdateProfile(authentication.getName());
    }

    @GetMapping("/me")
    public Long getMe(Authentication authentication) {
        Object id=userService.findMe(authentication.getName());

        return ((Number)id).longValue();
    }
    
    


    @GetMapping("/user-info")
    public UserInfo getMethodName(Authentication authentication) {
        String email=authentication.getName().trim().toLowerCase();
        return userService.getInfo(email);
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping("/name")
    public ResponseEntity<String> postMethodName(
            @RequestBody UsernameRequest usernameReq, 
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthenticated");
        }

        try {
            String email = authentication.getName().trim().toLowerCase();
            userService.userNameExist(email, usernameReq.getUsername(), usernameReq.getBio());
            return ResponseEntity.ok("Username set successfully");

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Username already taken");

        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
    
    @PutMapping("/change_info")
    public ResponseEntity<?> putMethodName(@RequestBody UpdateProfile entity,Authentication authentication) {
        String email=authentication.getName().trim().toLowerCase();

        userService.changeUser(email, entity);

        return ResponseEntity.ok("Profile Updated");
    }

    @GetMapping("/user-data/{id}")
    public UserDto getMethodName(@PathVariable Long id) {
        System.out.println("from chat request "+id);
        return userService.finduser(id);
    }

    @PostMapping("followandunfollow/{id}")
    public String toggleFollow(@PathVariable Long id, Authentication authentication){
        String email=authentication.getName().trim().toLowerCase();
        return userService.toggleFollow(email, id);
    }

    @PostMapping("/addBattery")
    public void addScore(@RequestBody Integer score,Authentication authentication) {
    
        System.out.println("this is my score "+score);
        String email=authentication.getName().toLowerCase().trim();
        userService.addBattery(email, score);
    }

    @GetMapping("/getPercentage")
    public Integer getBattery(Authentication authentication) {
        return userService.getScore(authentication.getName().trim().toLowerCase());
    }

    @GetMapping("/userCard")
    public UserCardDto getUserCardDto(Authentication authentication) {
        return userService.getUserCardDto(authentication.getName().trim().toLowerCase());
    }

    @GetMapping("/userStat")
    public PostStatDto getUserStat(Authentication authentication) {
        return userService.getPostStatDto(authentication.getName().trim().toLowerCase());
    }
    
    
    
    
    
    
    
}
