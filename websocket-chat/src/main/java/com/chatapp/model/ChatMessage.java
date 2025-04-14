package com.chatapp.model;
import java.time.LocalDateTime;

public class ChatMessage {
    private String content; //message text
    private MessageType type; //chat, join, or leave
    private String username; //username
    private LocalDateTime timestamp; //message timestamp

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }
    
    //getters and setters for chatmesssage class
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public MessageType getType() { return type; }
    public void setType(MessageType type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public LocalDateTime getTimestam() { return timestamp; } 
    public void setTImestamp(LocalDateTime timestamp) { this.timestamp = timestamp;}
}
