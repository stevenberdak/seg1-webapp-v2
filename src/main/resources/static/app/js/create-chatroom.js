function createChatroom() {
    const titleBox = document.getElementById("title-input")
    const headerBox = document.getElementById("header-input")
    const descriptionBox = document.getElementById("description-input")

    fetch("api/chatrooms",
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

            if (response.status !== 200) {
                throw new Error(`${json.status} ${json.message}`)
            } else {
                window.location.replace("/chatroom.html?title=" + titleBox.value)
            }
        })
        .catch(err => {
            alert(`Error: ${err}`)
        })
}

window.createChatroom = createChatroom