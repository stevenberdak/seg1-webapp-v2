let stompClient = null;
let currentRoomId = null;
let currentUsername = null;

async function loadMessageHistory() {
    if (!currentRoomId) return;

    const messageContainer = document.getElementById("message-container");
    if (!messageContainer) {
        console.error("Message container not found!");
        return;
    }
    messageContainer.innerHTML = '';

    try {
        const response = await fetch(`/api/chatrooms/${currentRoomId}/messages`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const history = await response.json();

        history.forEach(message => {
            // This assumes the DTO has { username: '...', content: '...', timestamp: '...' }
            showMessage({
                type: 'CHAT',
                username: message.username,
                content: message.content
            });
        });
        console.log("Message history loaded.");

        // Auto-scroll to the bottom after loading history
        messageContainer.scrollTop = messageContainer.scrollHeight;

    } catch (error) {
        console.error("Failed to load message history:", error);
        messageContainer.textContent = "Error loading message history."; // Inform user
    }
}

document.addEventListener('DOMContentLoaded', (event) => {
    const params = new URLSearchParams(window.location.search);
    const title = params.get("title");
    currentRoomId = params.get("roomId");
    const header = params.get("header");
    const description = params.get("description");

    try {
        if (title) {
            const titleElement = document.getElementById("chatroom-title");
            if (titleElement) titleElement.textContent = title;
            else console.error("Element with ID 'chatroom-title' not found!");
        }
        if (header) {
            const headerElement = document.getElementById("chatroom-header");
            if (headerElement) headerElement.textContent = header;
            else console.error("Element with ID 'chatroom-header' not found!");
        }
        if (description) {
            const descriptionElement = document.getElementById("chatroom-description");
            if (descriptionElement) descriptionElement.textContent = description;
            else console.error("Element with ID 'chatroom-description' not found!");
        }
    } catch (e) {
        console.error("Error setting text content:", e);
    }

    // --- Fetch User Status *BEFORE* Connecting ---
    fetch('/auth/status')
        .then(response => {
            if (!response.ok) {
                // Handle cases where the user might not be authenticated
                // Or if the server returns an error
                if (response.status === 401 || response.status === 403) {
                    console.error("User not authenticated. Redirecting to sign-in.");
                } else {
                    console.error(`Error fetching auth status: ${response.status}`);
                }
                throw new Error('Authentication failed or server error');
            }
            return response.json();
        })
        .then(user => {
            currentUsername = user.username;

            // --- Now Connect to WebSocket ---
            if (currentRoomId) {
                // --- Load history *BEFORE* connecting or just after starting connection ---
                loadMessageHistory().then(() => {
                     connect(); // Connect ONLY after getting username AND attempting to load history
                });
            } else {
                console.error("Room ID is missing from URL parameters.");
            }
        })
        .catch(error => {
            console.error("Failed to get user info or connect:", error);
            const messageContainer = document.getElementById("message-container");
            if (messageContainer) messageContainer.textContent = "Error initializing chat: Could not verify user.";
            // Disable input/button
            const sendButton = document.getElementById('send-button');
            if (sendButton) sendButton.disabled = true;
            const messageInput = document.getElementById('message-input');
            if (messageInput) messageInput.disabled = true;
        });

    // --- Attach event listeners (ensures they are attached regardless of fetch outcome) ---
    const sendButton = document.getElementById('send-button');
    if (sendButton) {
        sendButton.addEventListener('click', sendMessage);
    } else {
        console.error("Send button not found!");
    }
});

function connect() {
    if (!currentUsername) {
        console.error("Cannot connect without a username.");
        return;
    }
    const socket = new SockJS('/chat');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function(frame) {
        // Subscribe to the specific chat room topic
        const subscriptionDestination = '/topic/' + currentRoomId;
        stompClient.subscribe(subscriptionDestination, function(message) {
            showMessage(JSON.parse(message.body));
        });

        const addUserDestination = "/app/chat.addUser";
        const addUserPayload = {
            username: currentUsername,
            type: "JOIN",
            roomId: currentRoomId
        };

        try {
            stompClient.send(addUserDestination, {}, JSON.stringify(addUserPayload));
        } catch (e) {
        }

        // Enable input/button
        const sendButton = document.getElementById('send-button');
        if (sendButton) sendButton.disabled = false;
        const messageInput = document.getElementById('message-input');
        if (messageInput) messageInput.disabled = false;

    }, function(error) {
        console.error('STOMP connection error:', error);
    });

    // Disable input until connected
    const sendButton = document.getElementById('send-button');
    if (sendButton) sendButton.disabled = true;
    const messageInput = document.getElementById('message-input');
    if (messageInput) messageInput.disabled = true;
}

function sendMessage() {
    const messageInput = document.getElementById("message-input");
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            username: currentUsername,
            content: messageContent,
            type: "CHAT",
            roomId: currentRoomId
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
}

function showMessage(message) {
    const messageContainer = document.getElementById("message-container");
    const messageElement = document.createElement('div');
    messageElement.classList.add('message-item', 'p-2');

    // Customize how the message is displayed
    if (message.type === 'JOIN' || message.type === 'LEAVE') {
        messageElement.classList.add('text-gray-500', 'italic', 'text-sm'); // Style system messages
        messageElement.textContent = message.username + ' ' + (message.type === 'JOIN' ? 'joined' : 'left') + '!';
    } else { // CHAT message
        messageElement.classList.add('text-gray-800'); // Style chat messages
        const userSpan = document.createElement('span');
        userSpan.classList.add('font-semibold');
        userSpan.textContent = `[${message.username}] `;
        messageElement.appendChild(userSpan);
        messageElement.appendChild(document.createTextNode(message.content));
    }

    messageContainer.appendChild(messageElement);

    // Auto-scroll to the bottom
    messageContainer.scrollTop = messageContainer.scrollHeight;
}

document.addEventListener('DOMContentLoaded', (event) => {
    const sendButton = document.getElementById('send-button');
    if (sendButton) {
        sendButton.addEventListener('click', sendMessage);
    } else {
        console.error("Send button not found!");
    }

    const messageInput = document.getElementById('message-input');
    if (messageInput) {
        messageInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                sendMessage();
            }
        });
    } else {
        console.error("Message input not found!");
    }
});
