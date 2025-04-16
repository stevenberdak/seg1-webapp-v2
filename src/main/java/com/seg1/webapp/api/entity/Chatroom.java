package com.seg1.webapp.api.entity;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import javax.validation.constraints.Size;

import java.util.List;

@Entity
@Table(name = "chatrooms")
public class Chatroom {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime created_at;

    @Column(unique=true)
    @Size(min=5, message="Chatroom title must be at least 5 characters")
    private String title;
    private String header;

    @OneToMany(mappedBy = "id")
    private List<Participant> participants;

    @OneToMany(mappedBy = "id")
    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
    private String description;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public List<Participant> getParticipants() {
        return participants;
    }
    public void setParticipants(List<Participant> participant) {
        this.participants = participant;
    } 
}
