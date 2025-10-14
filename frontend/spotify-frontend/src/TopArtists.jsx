import React, { useEffect, useState, useRef } from "react";

function TopArtists({ userId }) {
    const [artists, setArtists] = useState([]);
    const [term, setTerm] = useState("medium_term");
    const intervalRef = useRef(null);

    useEffect(() => {
        if (!userId) return;

        const fetchArtists = () => {
            fetch(`http://localhost:8080/api/user-top-artists?userId=${userId}&time_range=${term}`)
                .then(res => res.json())
                .then(data => {
                    setArtists(data);

                    // Jeśli dane puste — sprawdzaj co 3 sekundy (polling)
                    if (data.length === 0 && term === "long_term") {
                        if (!intervalRef.current) {
                            intervalRef.current = setInterval(fetchArtists, 3000);
                        }
                    } else {
                        // Jeśli dane przyszły — zatrzymaj polling
                        clearInterval(intervalRef.current);
                        intervalRef.current = null;
                    }
                })
                .catch(err => console.error(err));
        };

        fetchArtists();

        // czyszczenie po zmianie userId/term
        return () => {
            clearInterval(intervalRef.current);
            intervalRef.current = null;
        };
    }, [userId, term]);

    return (
        <div style={{ textAlign: "center" }}>
            <h2>Top Artists</h2>

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

            {/* Lista artystów */}
            <div
                style={{
                    display: "flex",
                    flexWrap: "wrap",
                    justifyContent: "center",
                    gap: "16px"
                }}
            >
                {artists.length > 0 ? (
                    artists.map((artist, index) => (
                        <div
                            key={index}
                            style={{
                                width: "220px",
                                textAlign: "center",
                                boxShadow: "0 0 8px rgba(0,0,0,0.1)",
                                borderRadius: "12px",
                                padding: "12px"
                            }}
                        >
                            <a href={artist.spotifyUrl} target="_blank" rel="noopener noreferrer">
                                <img
                                    src={artist.imageUrl}
                                    alt={artist.name}
                                    style={{
                                        width: "200px",
                                        height: "200px",
                                        borderRadius: "8px",
                                        objectFit: "cover",
                                        display: "block",
                                        margin: "0 auto"
                                    }}
                                />
                            </a>
                            <h4 style={{ marginTop: "10px", fontSize: "16px" }}>{artist.name}</h4>
                        </div>
                    ))
                ) : (
                    <p>Loading your top artists...</p>
                )}
            </div>
        </div>
    );
}

export default TopArtists;
