package com.dev.ChatApp.SessionTracker;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionTracker {

    private final Set<String> activeUsers = ConcurrentHashMap.newKeySet();

    public void addUser(String username) {
        activeUsers.add(username);
    }

    public void removeUser(String username) {
        activeUsers.remove(username);
    }

    public Set<String> getActiveUsers() {
        return Collections.unmodifiableSet(activeUsers);
    }
}
