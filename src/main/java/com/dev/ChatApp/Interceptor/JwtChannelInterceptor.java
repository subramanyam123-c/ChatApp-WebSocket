package com.dev.ChatApp.Interceptor;

import com.dev.ChatApp.JWT.JwtUtil;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;  // Make sure this is imported
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    public JwtChannelInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Use StompCommand instead of StompHeaderAccessor.Command
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization"); // Extract from STOMP CONNECT

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // Remove "Bearer "
                try {
                    if (jwtUtil.validateToken(token)) {
                        String username = jwtUtil.extractUsername(token);
                        accessor.setUser(() -> username); // Set authenticated user
                        return message;
                    }
                } catch (Exception e) {
                    System.err.println("Token validation failed: " + e.getMessage());
                }
            }

            System.err.println("Invalid or missing Authorization header in STOMP CONNECT");
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }

        return message;
    }
}
