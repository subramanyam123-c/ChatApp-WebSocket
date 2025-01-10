package com.dev.ChatApp.EventListener;

import com.dev.ChatApp.Model.User;
import com.dev.ChatApp.Repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class WebSocketEventListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        String username = event.getUser().getName();
        logger.info("User connected: {}", username);

        // Update the user's status to "online"
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setStatus("online");
            userRepository.save(user);

            // Notify about the user's updated status
            notifyUserStatus(user);
        });
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        String username = event.getUser().getName();
        logger.info("User disconnected: {}", username);

        // Update the user's status to "offline" and set the last seen time
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setStatus("offline");
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);

            // Notify about the user's updated status
            notifyUserStatus(user);
        });
    }

    private void notifyUserStatus(User user) {
        // Send only the updated user's status
        Map<String, Object> statusUpdate = new HashMap<>();
        statusUpdate.put("username", user.getUsername());
        statusUpdate.put("status", user.getStatus());

        messagingTemplate.convertAndSend("/topic/userStatus", statusUpdate);
    }
}
