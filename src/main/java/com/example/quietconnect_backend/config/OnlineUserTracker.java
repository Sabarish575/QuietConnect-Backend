package com.example.quietconnect_backend.config;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class OnlineUserTracker {

    private static final Set<String> onlineUsers =
            ConcurrentHashMap.newKeySet();

    public void addOnlineUser(String id) {
        onlineUsers.add(id);
    }

    public void removeOfflineUser(String id) {
        onlineUsers.remove(id);
    }

    public boolean isOnline(String id) {
        return onlineUsers.contains(id);
    }

    public Set<String> getOnlineUsers() {
        return onlineUsers;
    }
}