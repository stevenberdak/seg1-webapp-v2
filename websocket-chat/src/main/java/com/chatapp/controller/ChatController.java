package com.chatapp.controller;

import com.chatapp.model.ChatMessage;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

	//handles sent messages
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        return chatMessage;
    }

    //handles when a user joins and stores their username
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")//need to update when we have multiple chatrooms figured out
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // save username in session so it can be used in future messages
    	headerAccessor.getSessionAttributes().put("username", chatMessage.getUsername());
        return chatMessage;
    }
}
