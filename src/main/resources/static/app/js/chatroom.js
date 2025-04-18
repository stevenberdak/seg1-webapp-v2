window.onload = function () {
    const params = new URLSearchParams(window.location.search);
    const title = params.get("title");
    const header = params.get("header");
    const description = params.get("description");
  
    if (title) {
        document.getElementById("chatroom-title").textContent = title;
    }
  
    if (header) {
        document.getElementById("chatroom-header").textContent = header;
    }
  
    if (description) {
        document.getElementById("chatroom-description").textContent = description;
    }
  };