package com.dev.ChatApp.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomHandshakeHandler.class);

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String username = (String) attributes.get("username");
        if (username != null) {
            logger.info("Assigning Principal for username: {}", username);
            return new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
        } else {
            logger.warn("Username not found in attributes. Principal not assigned.");
            return null;
        }
    }
}
