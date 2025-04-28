async function fetchAuthStatus() {
    try {
        const response = await fetch('/auth/status', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',


                'Accept': 'application/json'

            }
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const authStatus = await response.json();
        this.updateAuthStatus(authStatus);
    }
    catch (error) {
        console.error('Error fetching auth status:', error);
    }
}

// Update the UI based on the authentication status
function updateAuthStatus(authStatus) {
    const authStatusElement = document.getElementById('username-display');
    if (authStatusElement) {
        authStatusElement.textContent = authStatus.username;
    }
}

window.addEventListener('DOMContentLoaded', () => {
    fetchAuthStatus();
});
