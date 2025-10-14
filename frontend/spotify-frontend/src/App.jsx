import React, { useState, useEffect } from "react";
import Login from "./Login";
import TopTracks from "./TopTracks";
import TopArtists from "./TopArtists";

function App() {
    const [userId, setUserId] = useState(null);
    const [view, setView] = useState("tracks"); // "tracks" lub "artists"

    useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const id = params.get("userId");
        if (id) setUserId(id);
    }, []);

    if (!userId) return <Login />;

    return (
        <div style={{ textAlign: "center" }}>
            {/* Pasek przełączania widoku */}
            <div style={{ margin: "20px 0" }}>
                <button
                    onClick={() => setView("tracks")}
                    style={{
                        padding: "10px 20px",
                        marginRight: "10px",
                        borderRadius: "8px",
                        border: "none",
                        backgroundColor: view === "tracks" ? "#1DB954" : "#ccc",
                        color: view === "tracks" ? "white" : "black",
                        cursor: "pointer"
                    }}
                >
                    Top Tracks
                </button>
                <button
                    onClick={() => setView("artists")}
                    style={{
                        padding: "10px 20px",
                        borderRadius: "8px",
                        border: "none",
                        backgroundColor: view === "artists" ? "#1DB954" : "#ccc",
                        color: view === "artists" ? "white" : "black",
                        cursor: "pointer"
                    }}
                >
                    Top Artists
                </button>
            </div>

            {/* Widok dynamiczny */}
            {view === "tracks" ? <TopTracks userId={userId} /> : <TopArtists userId={userId} />}
        </div>
    );
}

export default App;
