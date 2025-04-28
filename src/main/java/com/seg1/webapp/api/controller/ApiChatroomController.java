package com.seg1.webapp.api.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.seg1.webapp.api.entity.Chatroom;
import com.seg1.webapp.api.entity.Message;
import com.seg1.webapp.api.entity.User;
import com.seg1.webapp.api.repository.MessageRepository;
import com.seg1.webapp.api.repository.UserRepository;
import com.seg1.webapp.api.dto.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Comparator;
import com.seg1.webapp.api.repository.ChatroomRepository;
import com.seg1.webapp.api.dto.ChatroomListDto;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;


import java.util.List;

// https://chatgpt.com/c/67fff773-fd30-8010-88dd-bbe7aeb8351c

@RestController
@RequestMapping("/api/chatrooms")
public class ApiChatroomController {
    private final ChatroomRepository repo;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    public ApiChatroomController(ChatroomRepository repo) {
        this.repo = repo;
    }

    @GetMapping // Modified method
    public List<ChatroomListDto> getAll() {
        List<Chatroom> chatrooms = repo.findAll();
        // Map the entities to DTOs
        return chatrooms.stream()
                .map(chatroom -> new ChatroomListDto(
                        chatroom.getId(),
                        chatroom.getTitle(),
                        chatroom.getHeader(),
                        chatroom.getDescription()
                ))
                .collect(Collectors.toList());
    }
    @GetMapping("/{id}")
    public Chatroom getById(@PathVariable Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chatroom not found"));
    }

    @PostMapping
    public Chatroom create(@RequestBody Chatroom chatroom) {
        try {
            return repo.save(chatroom);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chatroom with the given name already exists", e);
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    @GetMapping("/{roomId}/messages")
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<MessageDto> getMessageHistory(@PathVariable Long roomId) {
        if (!repo.existsById(roomId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Chatroom not found");
        }
        List<Message> messages = messageRepository.findByChatroomId(roomId);
        List<Long> userIds = messages.stream().map(Message::getUserId).distinct().toList();
        Map<Long, String> userIdToUsernameMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        return messages.stream()
                .sorted(Comparator.comparing(Message::getCreated_at))
                .map(message -> new MessageDto(
                        userIdToUsernameMap.getOrDefault(message.getUserId(), "Unknown User"),
                        message.getContent(),
                        message.getCreated_at()
                ))
                .collect(Collectors.toList());
    }
}
