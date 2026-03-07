package com.example.quietconnect_backend.message;

import java.security.Principal;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import com.example.quietconnect_backend.config.OnlineUserTracker;
import com.example.quietconnect_backend.dto.ChatContentDto;
import com.example.quietconnect_backend.dto.MessageDto;
import com.example.quietconnect_backend.dto.UserDto;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/chat")
public class ChatController {



    @Autowired
    private ChatService chatService;

    @GetMapping("/getOldchat/{receiverId}")
    public List<ChatContentDto> getOldChat(Authentication auth,@PathVariable Long receiverId) throws Exception {
        
        return chatService.getAllMessage(auth.getName(), receiverId);
    }

    @GetMapping("/getFriend")
    public ResponseEntity<List<UserDto>> getMethodName(Authentication authentication) {
        List<UserDto> ans=chatService.getChatFriend(authentication.getName());
        if(ans==null || ans.isEmpty()){
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok().body(ans);   
    }

    
    

    
    



}
