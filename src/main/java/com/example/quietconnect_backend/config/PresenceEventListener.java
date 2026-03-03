package com.example.quietconnect_backend.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.example.quietconnect_backend.dto.PresenceDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PresenceEventListener {

    private final OnlineUserTracker tracker;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) accessor.getSessionAttributes().get("email");
        if (userId != null && !userId.isBlank()) {
            tracker.addOnlineUser(userId);
            broadcastStatus(userId, true);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = (String) accessor.getSessionAttributes().get("email");

        if (userId != null && !userId.isBlank()) {
            tracker.removeOfflineUser(userId);
            broadcastStatus(userId, false);
        }
    }

    private void broadcastStatus(String userId, boolean online) {
        messagingTemplate.convertAndSend(
                "/topic/presence",
                new PresenceDto(userId, online)
        );
    }
}