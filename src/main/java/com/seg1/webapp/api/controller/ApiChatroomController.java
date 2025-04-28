package com.seg1.webapp.api.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.seg1.webapp.api.dto.ChatroomDto;
import com.seg1.webapp.api.dto.MessageDto;
import com.seg1.webapp.api.entity.Chatroom;
import com.seg1.webapp.api.entity.Message;
import com.seg1.webapp.api.entity.User;
import com.seg1.webapp.api.repository.ChatroomRepository;
import com.seg1.webapp.api.repository.MessageRepository;
import com.seg1.webapp.api.repository.UserRepository;

import jakarta.transaction.Transactional;

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
    public List<ChatroomDto> getAll() {
        List<Chatroom> chatrooms = repo.findAll();
        // Map the entities to DTOs
        return chatrooms.stream()
                .map(chatroom -> new ChatroomDto(
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
