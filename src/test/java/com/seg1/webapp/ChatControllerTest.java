package com.seg1.webapp.websocket.controller;

import com.seg1.webapp.websocket.model.ChatMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.lang.reflect.Type;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import com.seg1.webapp.api.entity.Chatroom;
import com.seg1.webapp.api.entity.User;
import com.seg1.webapp.api.repository.UserRepository;
import com.seg1.webapp.api.repository.ChatroomRepository;

//Still working on this test file. Have been working with ChatGPT to try and get the test to 
//work but running into issues. Still a work in progress and continuation for debugging. 

//boots up spring application and runs on random port for testing
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChatroomRepository chatroomRepository;

	@LocalServerPort
	private int port;
	private String getWebSocketUri() {
		return "ws://localhost:" + port + "/chat";
	}
	private final String TOPIC_DEST = "/topic/1";  // assuming roomId is 1
	private final String SEND_DEST = "/app/chat.sendMessage";

	@Test
	public void testSendAndReceiveChatMessage() throws Exception {
		messageRepository.deleteAllByRoomId(1L);
		
		userRepository.findByUsername("JUnit").ifPresent(userRepository::delete);
		chatroomRepository.findById(1L).ifPresent(chatroomRepository::delete);
		
		User user = new User();
		user.setUsername("JUnit");
		user.setPasswordHash("fakePassword123");
		userRepository.save(user);

		Chatroom chatroom = new Chatroom();
		chatroom.setTitle("JUnitRoom");
		chatroom.setHeader("JUnit Header"); // if required
		chatroom.setColor("#000000"); // if required
		chatroom = chatroomRepository.save(chatroom); // important: reassign to get generated ID

		Long roomId = chatroom.getId();
		String dynamicTopicDest = "/topic/" + roomId;
		String dynamicSendDest = "/app/chat.sendMessage";

		//builds a test websocket client that will use STOMP and JSON 
		WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
		stompClient.setMessageConverter(new MappingJackson2MessageConverter());

		//wait for response without blocking, when message received, it will complete
		CompletableFuture<ChatMessage> completableFuture = new CompletableFuture<>();

		//joins roomid 1, and listens for incoming message
		StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
			@Override
			public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
				System.out.println("CONNECTED to server");

				session.subscribe(TOPIC_DEST, new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						return ChatMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						completableFuture.complete((ChatMessage) payload);
					}
				});

				try {
					Thread.sleep(1000); // give time for subscriptions to initialize
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				ChatMessage message = new ChatMessage();
				message.setUsername("JUnit");
				message.setContent("Hello, test!");
				message.setRoomId(roomId);

				session.send(dynamicSendDest, message); //send the test message to controller
			}
		};

		//wait 5 seconds for response to arrive 
		//message should be received if server sent correctly 
		stompClient.connect(getWebSocketUri(), new WebSocketHttpHeaders(), sessionHandler);
		ChatMessage response = completableFuture.get(5, TimeUnit.SECONDS);

		assertNotNull(response);
		assertEquals("JUnit", response.getUsername());
		assertEquals("Hello, test!", response.getContent());
	}
}