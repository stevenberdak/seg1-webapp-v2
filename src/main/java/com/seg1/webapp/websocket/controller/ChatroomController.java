package com.seg1.webapp.websocket.controller;

import com.seg1.webapp.websocket.model.ChatMessage;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;

@Controller
public class ChatroomController {

    //handles sent messages
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessage;
    }

    //handles when a user joins and stores their username
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public") //need to update when we have multiple chatrooms figured out
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // save username in session so it can be used in future messages
        headerAccessor.getSessionAttributes().put("username", chatMessage.getUsername());
        chatMessage.setTimestamp(LocalDateTime.now());
        return chatMessage;
    }
}