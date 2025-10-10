import React, { useEffect, useState } from "react";

function TopTracks({ userId }) {
    const [tracks, setTracks] = useState([]);
    const [term, setTerm] = useState("medium_term");
    const [artist, setArtist] = useState("");
    const [searchMode, setSearchMode] = useState(false);

    useEffect(() => {
        if (!userId) return;

        fetch(`http://localhost:8080/api/user-top-tracks?userId=${userId}&time_range=${term}`)
            .then(res => res.json())
            .then(data => setTracks(data))
            .catch(err => console.error(err));
    }, [userId, term]);

    const handleSearch = () => {
        if (artist.trim() === "") {
            setSearchMode(false);
            fetch(`http://localhost:8080/api/user-top-tracks?userId=${userId}&time_range=${term}`)
                .then(res => res.json())
                .then(data => setTracks(data))
                .catch(err => console.error(err));
            return;
        }

        fetch(
            `http://localhost:8080/api/user-top-tracks/by-artist?userId=${userId}&artist=${encodeURIComponent(
                artist
            )}`
        )
            .then(res => res.json())
            .then(data => {
                setTracks(data);
                setSearchMode(true);
            })
            .catch(err => console.error(err));
    };

    return (
        <div style={{ textAlign: "center" }}>
            <h2>Top Tracks</h2>

            {/* Wybór zakresu czasowego */}
            <div style={{ marginBottom: "20px" }}>
                <label htmlFor="term">Select Time Range: </label>
                <select
                    id="term"
                    value={term}
                    onChange={e => setTerm(e.target.value)}
                    style={{
                        padding: "8px",
                        borderRadius: "8px",
                        border: "1px solid #ccc",
                        fontSize: "14px"
                    }}
                >
                    <option value="short_term">Last 4 weeks</option>
                    <option value="medium_term">Last 6 months</option>
                    <option value="long_term">All time</option>
                </select>
            </div>

            {/* Wyszukiwanie po artyście — tylko dla long_term */}
            {term === "long_term" && (
                <div style={{ marginBottom: "20px" }}>
                    <input
                        type="text"
                        placeholder="Search by artist..."
                        value={artist}
                        onChange={e => setArtist(e.target.value)}
                        style={{
                            padding: "8px",
                            width: "250px",
                            borderRadius: "8px",
                            border: "1px solid #ccc",
                            marginRight: "10px"
                        }}
                    />
                    <button
                        onClick={handleSearch}
                        style={{
                            padding: "8px 16px",
                            borderRadius: "8px",
                            backgroundColor: "#1DB954",
                            color: "white",
                            border: "none",
                            cursor: "pointer"
                        }}
                    >
                        Search
                    </button>
                </div>
            )}

            {/* Lista piosenek */}
            <div style={{ display: "flex", flexWrap: "wrap", justifyContent: "center" }}>
                {tracks.length > 0 ? (
                    tracks.map((track, index) => (
                        <div
                            key={index}
                            style={{
                                margin: "10px",
                                width: "200px",
                                textAlign: "center",
                                boxShadow: "0 0 8px rgba(0,0,0,0.1)",
                                borderRadius: "12px",
                                padding: "10px"
                            }}
                        >
                            <a href={track.songUrl} target="_blank" rel="noopener noreferrer">
                                <img
                                    src={track.imageUrl}
                                    alt={track.title}
                                    style={{
                                        width: "100%",
                                        borderRadius: "8px"
                                    }}
                                />
                            </a>
                            <h4>{track.title}</h4>
                            <p>{track.artist}</p>
                        </div>
                    ))
                ) : (
                    <p>{searchMode ? "No songs found for this artist." : "Loading your top tracks..."}</p>
                )}
            </div>
        </div>
    );
}

export default TopTracks;
