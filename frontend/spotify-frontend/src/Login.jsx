import React from "react";

function Login() {
    const handleLogin = () => {
        // backend call
        fetch("http://localhost:8080/api/login")
            .then(res => res.text())
            .then(url => {
                window.location.replace(url); // redirect to spotify login
            });
    };

    return (
        <div className="login-container">
            <button onClick={handleLogin}>Login with Spotify</button>
        </div>
    );
}

export default Login;
