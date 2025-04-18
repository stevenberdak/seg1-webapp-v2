To connect backend to front end, const socket = new SockJS('/chat'); will need to be added for local testing.
When testing from a website we choose, const socket = new SockJS('https://websitename/chat'); will need to be added
for it to connect appropriately.

Websocket Config sets up the websocket connection. It handles the sending and 
receiving of messages through spring boot with the /app and /topic routes. 

Chat controller handles the chat room logic and where the messages are sent and received to
ensuring they are sent to the correct chat room and broacasts to users. 

Chatmessage.java contains all of the getters and setters and message structure. 

For testing in Eclipse on a local machine, websocketchatapplication.java needs to be right clicked, 
and then run as -> java application, then head to the website on chrome: http://localhost:8080/chat-test.html and you
should be able to test it. 