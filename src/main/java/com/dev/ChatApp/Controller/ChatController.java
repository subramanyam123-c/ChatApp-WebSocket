package com.dev.ChatApp.Controller;

import com.dev.ChatApp.JWT.JwtUtil;
import com.dev.ChatApp.Model.Message;
import com.dev.ChatApp.Model.MessageRequest;
import com.dev.ChatApp.Model.User;
import com.dev.ChatApp.Service.MessageService;
import com.dev.ChatApp.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller

public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @GetMapping("/api/chat/history")
    public ResponseEntity<List<Message>> getChatHistory(
            @RequestHeader("Authorization") String token,
            @RequestParam String receiver) {
        try {
            String senderUsername = jwtUtil.extractUsername(token.replace("Bearer ", ""));
            logger.info("Token: {}", token);
            logger.info("Extracted username: {}", senderUsername);
            System.out.println("senderUsername"+senderUsername+"to"+receiver);
            List<Message> messages = messageService.getMessagesBetweenUsers(senderUsername, receiver);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MessageRequest messageRequest, Principal principal) {
        logger.info("Received message request: {}", messageRequest);

        if (principal == null) {
            logger.error("Principal is null. User not authenticated.");
            return;
        }

        String senderUsername = principal.getName();
        logger.debug("Sender username from principal: {}", senderUsername);

        // Fetch sender's full information
        User sender = userService.findUser(senderUsername);
        if (sender == null) {
            logger.error("Sender not found with username: {}", senderUsername);
            return;
        }

        // Fetch receiver's information
        String receiverUsername = messageRequest.getReceiverUsername();
        User receiver = userService.findUser(receiverUsername);
        if (receiver == null) {
            logger.error("Receiver not found with username: {}", receiverUsername);
            return;
        }

        // Create and populate the Message entity
        Message message = new Message();
        message.setSender(senderUsername);
        message.setReceiver(receiverUsername);
        message.setContent(messageRequest.getContent());
        message.setCreatedAt(LocalDateTime.now());
        message.setStatus("SENT");

        // Save and send the message
        messageService.saveMessage(message);

        logger.info("Message saved: {}", message);
        logger.info("URL:"+receiverUsername+ "/queue/messages");
        messagingTemplate.convertAndSendToUser(
                receiverUsername,
                "/queue/messages",
//                message
                messageRequest
        );

        logger.info("Message sent to user: {}", receiver.getUsername());
    }

    @GetMapping("/username")
    public ResponseEntity<String> getUsername(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Extract the token from the Bearer header
            String token = authorizationHeader.replace("Bearer ", "");
            String username = jwtUtil.extractUsername(token);
            return ResponseEntity.ok(username);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers(@RequestHeader("Authorization") String token) {
        try {
            // Extract the logged-in user's username
            String currentUsername = jwtUtil.extractUsername(token.replace("Bearer ", ""));

            // Fetch all users except the logged-in user
            List<User> allUsers = userService.getAllUsersExcluding(currentUsername);

            // Map the user data to include only necessary fields
            List<Map<String, Object>> usersResponse = allUsers.stream().map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("username", user.getUsername());
                userMap.put("status", user.getStatus()); // Online, Offline, etc.
                userMap.put("profilePictureUrl", user.getProfilePictureUrl()); // Optional, if you use profile pictures
                return userMap;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(usersResponse);
        } catch (Exception e) {
            logger.error("Error fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
