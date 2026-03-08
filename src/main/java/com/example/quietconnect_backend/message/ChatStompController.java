package com.example.quietconnect_backend.message;

import java.security.Principal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quietconnect_backend.config.OnlineUserTracker;
import com.example.quietconnect_backend.dto.MessageDto;

@RestController
public class ChatStompController {

        @Autowired
        private ChatService chatService;
        @Autowired
        private OnlineUserTracker tracker;
        @MessageMapping("/private-message")
    public void sendMessages(MessageDto message,Principal principal) throws Exception{
        chatService.sendMessage(message,principal.getName());
    }

        @GetMapping("/presence/online")
    public Set<String> getOnlineUsers() {
        Set<String> res=tracker.getOnlineUsers();
        return res;
    }
}
