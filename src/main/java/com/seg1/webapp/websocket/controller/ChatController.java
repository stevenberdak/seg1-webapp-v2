package com.seg1.webapp.websocket.controller;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import com.seg1.webapp.api.entity.Chatroom;
import com.seg1.webapp.api.entity.Message;
import com.seg1.webapp.api.entity.Participant;
import com.seg1.webapp.api.entity.User;
import com.seg1.webapp.api.repository.ChatroomRepository;
import com.seg1.webapp.api.repository.MessageRepository;
import com.seg1.webapp.api.repository.ParticipantRepository;
import com.seg1.webapp.api.repository.UserRepository;
import com.seg1.webapp.websocket.model.ChatMessage;

@Controller
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatroomRepository chatroomRepository;
    @Autowired
    private ParticipantRepository participantRepository;

    // Variables for reentrant lock setup
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ReentrantLock lock = new ReentrantLock();

    // Handles sending a chat message
    @MessageMapping("/chat.sendMessage")
    @Transactional
    public void sendMessage(@Payload ChatMessage chatMessage) {
        executor.submit(() -> {
            lock.lock(); // Acquire the lock
            try {
                // --- Save the message to the database BEFORE broadcasting ---
                try {
                    // Find the user - handle case where user might not exist
                    User user = userRepository.findByUsername(chatMessage.getUsername())
                            .orElseThrow(() -> new RuntimeException("User not found: " + chatMessage.getUsername()));

                    // Find the chatroom - handle case where room might not exist
                    Long chatroomId = chatMessage.getRoomId();
                    Chatroom chatroom = chatroomRepository.findById(chatroomId)
                            .orElseThrow(() -> new RuntimeException("Chatroom not found: " + chatMessage.getRoomId()));

                    // Create the Message entity
                    Message messageEntity = new Message();
                    messageEntity.setContent(chatMessage.getContent());
                    messageEntity.setUserId(user.getId()); // Store user ID
                    messageEntity.setChatroom(chatroom); // Set the relationship

                    // Save the message
                    messageRepository.save(messageEntity);
                    logger.info(">>> Saved message from {} to room {}", chatMessage.getUsername(),
                            chatMessage.getRoomId());

                } catch (NumberFormatException e) {
                    logger.error("!!! Invalid Room ID format: {}", chatMessage.getRoomId());
                    return;
                } catch (Exception e) {
                    logger.error("!!! Error saving message for user {} in room {}: {}",
                            chatMessage.getUsername(), chatMessage.getRoomId(), e.getMessage(), e);
                    return;
                }

                // Broadcast the message via WebSocket (only if saving was successful)
                messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
                logger.info(">>> Broadcasted message from {} to /topic/{}", chatMessage.getUsername(),
                        chatMessage.getRoomId());
            } catch (Exception e) {
                logger.error("!!! Error processing message for user {} in room {}: {}",
                        chatMessage.getUsername(), chatMessage.getRoomId(), e.getMessage(), e);
                e.printStackTrace();
            } finally {
                lock.unlock(); // Always unlock in a finally block
            }
        });

    }

    // Handles when a user joins and stores their username
    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Store username in session attributes
        headerAccessor.getSessionAttributes().put("username", chatMessage.getUsername());
        headerAccessor.getSessionAttributes().put("room_id", chatMessage.getRoomId());

        if (chatMessage.getType().equals(ChatMessage.MessageType.JOIN)) {
            Participant participant = new Participant();
            participant.setUsername(chatMessage.getUsername());
            participant.setChatroomId(chatMessage.getRoomId());
            participantRepository.save(participant);

            broadcastParticipantList(chatMessage.getRoomId());
        }

        // Broadcast the message
        try {
            messagingTemplate.convertAndSend("/topic/" + chatMessage.getRoomId(), chatMessage);
            logger.info(">>> Broadcasted JOIN message for user {} to /topic/{}", chatMessage.getUsername(),
                    chatMessage.getRoomId());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("!!! Error broadcasting JOIN message for user {} to /topic/{}", chatMessage.getUsername(),
                    chatMessage.getRoomId());
        }
    }

    public void broadcastParticipantList(Long roomId) {
        List<Participant> participants = participantRepository.findByChatroomId(roomId);
        List<String> usernames = participants.stream()
                .map(Participant::getUsername)
                .collect(Collectors.toList());

        messagingTemplate.convertAndSend("/topic/" + roomId + "/participants", usernames);
    }
}
