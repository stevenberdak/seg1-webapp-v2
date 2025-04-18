package com.chatapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
    	//Enables simple broker to broadcast messages to clients subscribed to /topic/{roomId}
        registry.enableSimpleBroker("/topic");
        // Routes client messages with /app prefix to server-side controller methods
        registry.setApplicationDestinationPrefixes("/app");
    }

    //Registers the WebSocket connection endpoint at /chat for SockJS clients
    //sets up the raw connection beteween browser and server
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat").setAllowedOriginPatterns("*").withSockJS();
    }
}
