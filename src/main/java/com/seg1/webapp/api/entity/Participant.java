package com.seg1.webapp.api.entity;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    private LocalDateTime created_at;

    @ManyToOne
    private Chatroom chatroom;

    @OneToOne
    private User user;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    private String role;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getRole() {
        return role;
    }

    public Chatroom getChatroom() {
        return chatroom;
    }
    public void setChatroom(Chatroom chatroom) {
        this.chatroom = chatroom;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
