function createChatroom() {
    const titleBox = document.getElementById("title-input")
    const headerBox = document.getElementById("header-input")
    const descriptionBox = document.getElementById("description-input")

    const apiEndpoint = `api/chatroom/create?title=${titleBox.value}&header=${headerBox.value}&description=${descriptionBox.value}`

    fetch(apiEndpoint).then(response => {
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