function greeting() {
    const inputBox = document.getElementById("name")

    const greetingBox = document.getElementById("server-response")

    const greetingUrl = `api/greeting?name=${inputBox.value}`

    fetch(greetingUrl).then(response => {
        return response.text()
    })
        .then(text => {
            greetingBox.innerText = text
        })
        .catch(err => {
            greetingBox.innerText = `Error: ${err}`
        })
}

window.greeting = greeting