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
        }).then(response => {
            return response.text()
        })
        .then(text => {
            alert(text)
        })
        .catch(err => {
            alert(`Error: ${err}`)
        })
}

window.createChatroom = createChatroom