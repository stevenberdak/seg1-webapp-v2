function listBrowsableChatrooms() {
    const chatroomListBox = document.getElementById("chatrooms-list-box")

    //    const mock_chatrooms = [
    //        { title: "Love Dogs", header: "Love for Dogs", description: "Talk about your love for your doggo pets!" },
    //        { title: "Cats Are Better", header: "Discussing Superiority of Cats", description: "Cats are the ultimate beings, there is not comparison" },
    //        { title: "Shoes", header: "Why Not Shoes?", description: "This chat is all about shoes, not socks" },
    //        { title: "I Love Sleep", header: "I Just Need to Sleep More", description: "Talk about sleep and relating things. I'm falling aslepp right now in fact" },
    //        { title: "Socks", header: "Why Shoes?", description: "This chat is all about socks, shoes smell" },
    //        { title: "Sanwiches", header: "Sandwich Culture", description: "Discuss the various ways that sandwiches can be made" },
    //        { title: "JavaScript Programmers Unites", header: "The Ultimate Programming Language", description: "Discuss the pros and greater pros of JavaScript" },
    //        { title: "Over 50 Group Chat", header: "Older People Being Mature", description: "Connect with people who can relate to being over 50" }
    //    ]

    const colors = [
        "#7438DA",
        "#F30305",
        "#14C726",
        "#0842FC",
        "#F59206",
        "#42FA6A",
        "#13BFEF",
    ]
    let colorIdx = 0
    const titleBoxSize = 40

    fetch("/api/chatrooms")
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(chatrooms => {
            chatrooms.forEach(chatroom => {
                const chatroomItem = document.createElement("a");

                const urlParams = new URLSearchParams({
                    roomId: chatroom.id,
                    title: chatroom.title,
                    header: chatroom.header || '',
                    description: chatroom.description || ''
                });
                Object.assign(chatroomItem, {
                    href: `/app/chatroom.html?${urlParams.toString()}`
                });

                chatroomItem.classList.add("block", "basis-1/4", `max-w-${titleBoxSize}`);

                const chatroomTitleBox = document.createElement("div");
                Object.assign(chatroomTitleBox.style, {
                    backgroundColor: colors[colorIdx],
                    color: "white",
                    padding: "8px"
                });
                chatroomTitleBox.classList.add(
                    `min-h-${titleBoxSize}`, `min-w-${titleBoxSize}`,
                    `max-h-${titleBoxSize}`, `max-w-${titleBoxSize}`,
                    `rounded-md`
                );

                colorIdx = (colorIdx + 1) % colors.length;

                // Create and append elements using data from the fetched 'chatroom' object
                const chatroomTitle = document.createElement("p");
                chatroomTitle.textContent = chatroom.title;
                chatroomTitle.classList.add("font-medium", "text-lg");

                const chatroomHeader = document.createElement("p");
                chatroomHeader.textContent = chatroom.header;
                chatroomHeader.classList.add("font-medium", "text-gray-800", "text-sm");

                const chatroomDescription = document.createElement("p");
                chatroomDescription.textContent = chatroom.description;
                chatroomDescription.classList.add("text-gray-500", "text-xs");

                // Append elements to build the item
                chatroomTitleBox.appendChild(chatroomTitle);
                chatroomItem.appendChild(chatroomTitleBox);
                chatroomItem.appendChild(chatroomHeader);
                chatroomItem.appendChild(chatroomDescription);

                // Add the complete item to the list box
                chatroomListBox.appendChild(chatroomItem);
            });
        })
        .catch(error => {
            console.error("Error fetching chatrooms:", error);
            chatroomListBox.textContent = "Failed to load chatrooms.";
        });
}

function listMyChatrooms() {
    // need to implement
}


window.addEventListener("DOMContentLoaded", () => {
    listBrowsableChatrooms();
    listMyChatrooms();
});
