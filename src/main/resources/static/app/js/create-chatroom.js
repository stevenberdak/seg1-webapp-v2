function createChatroom() {
    const titleBox = document.getElementById("title-input")
    const headerBox = document.getElementById("header-input")
    const descriptionBox = document.getElementById("description-input")

    fetch("/api/chatrooms",
        {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                title: titleBox.value,
                header: headerBox.value,
                description: descriptionBox.value
            })
        }).then(async response => {
            const json = await response.json()

            if (!response.ok) {
                const errorMessage = json?.message || `HTTP error ${response.status}`;
                throw new Error(errorMessage);
            } else {
                const urlParams = new URLSearchParams({
                    roomId: json.id,
                    title: json.title,
                    header: json.header || '',
                    description: json.description || ''
                });
                window.location.replace(`/app/chatroom.html?${urlParams.toString()}`);
            }
        })
        .catch(err => {
            console.error(err)
        })
}

window.createChatroom = createChatroom
