package com.dev.ChatApp.Configuration;

import com.dev.ChatApp.Handler.CustomHandshakeHandler;
import com.dev.ChatApp.Interceptor.JwtChannelInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // Enables WebSocket message handling
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    private final JwtChannelInterceptor jwtChannelInterceptor;
    private final CustomHandshakeHandler customHandshakeHandler;

    // Constructor-based injection (preferred)
    public WebSocketConfig(JwtChannelInterceptor jwtChannelInterceptor, CustomHandshakeHandler customHandshakeHandler) {
        this.jwtChannelInterceptor = jwtChannelInterceptor;
        this.customHandshakeHandler = customHandshakeHandler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        logger.info("Registering WebSocket endpoint at /ws");
        registry.addEndpoint("/ws")
                .setHandshakeHandler(customHandshakeHandler) // Register custom handshake handler
                .setAllowedOrigins("*"); // Allow all origins (adjust as per your CORS policy)
        //.withSockJS(); // Uncomment if SockJS fallback is needed
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Prefix for client-sent messages
        registry.setApplicationDestinationPrefixes("/app");

        // Enable a simple in-memory message broker
        registry.enableSimpleBroker("/topic", "/queue");

        // Prefix for user-specific messaging
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Register JwtChannelInterceptor for STOMP messages
        registration.interceptors(jwtChannelInterceptor);
    }
}
