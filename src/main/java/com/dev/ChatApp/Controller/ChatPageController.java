package com.dev.ChatApp.Controller;

import com.dev.ChatApp.JWT.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
public class ChatPageController {
    private static final Logger logger = LoggerFactory.getLogger(ChatPageController.class);
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/chat")
    public ResponseEntity<String> serveChatPage(@RequestHeader("Authorization") String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            logger.info("****************" + authorization);
            String token = authorization.substring(7);
            if (jwtUtil.validateToken(token)) {
                try {
                    // Load the file as a classpath resource
                    Resource resource = new ClassPathResource("static/chat.html");
                    String chatPageContent = new String(resource.getInputStream().readAllBytes());
                    logger.info("****************" );
                    //logger.info("****************" + chatPageContent);
                    return ResponseEntity.ok(chatPageContent);
                } catch (IOException e) {
                    logger.error("Error loading chat page content: ", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading chat page.");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
    }

}
