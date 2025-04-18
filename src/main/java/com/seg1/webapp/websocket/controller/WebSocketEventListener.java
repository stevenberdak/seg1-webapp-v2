package com.seg1.webapp.websocket.controller;

import com.seg1.webapp.websocket.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Retrieve username and room_id stored in the session during 'addUser'
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");

        if (username != null && roomId != null) {
            logger.info("User Disconnected: Username = {}, Room ID = {}", username, roomId);

            // LEAVE message
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setUsername(username);
            chatMessage.setRoomId(roomId);

            // Broadcast the LEAVE message to the specific room topic
            try {
                messagingTemplate.convertAndSend("/topic/" + roomId, chatMessage);
                logger.info("<<< LEAVE message broadcasted to /topic/{} for user {}", roomId, username);
            } catch (Exception e) {
                logger.error("!!! ERROR broadcasting LEAVE message for user {} to /topic/{}", username, roomId, e);
            }
        } else {
            logger.warn("Could not retrieve username or roomId from session attributes on disconnect. Session ID: {}", headerAccessor.getSessionId());
        }
    }
}
