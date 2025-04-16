package com.seg1.webapp.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.seg1.webapp.api.entity.Chatroom;
import com.seg1.webapp.api.repository.ChatroomRepository;
import org.springframework.http.HttpStatus;


import java.util.List;

// https://chatgpt.com/c/67fff773-fd30-8010-88dd-bbe7aeb8351c

@RestController
@RequestMapping("/api/chatrooms")
public class ApiChatroomController {
    private final ChatroomRepository repo;

    public ApiChatroomController(ChatroomRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Chatroom> getAll() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public Chatroom getById(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chatroom not found"));
    }

    @PostMapping
    public Chatroom create(@RequestBody Chatroom chatroom) {
        return repo.save(chatroom);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
