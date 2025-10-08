import React, { useEffect, useState } from "react";

function TopTracks({ userId }) {
    const [tracks, setTracks] = useState([]);
    const [term, setTerm] = useState("medium_term"); //default 6 months

    useEffect(() => {
        if (!userId) return;

        fetch(`http://localhost:8080/api/user-top-tracks?userId=${userId}&time_range=${term}`)
            .then(res => res.json())
            .then(data => setTracks(data))
            .catch(err => console.error(err));
    }, [userId, term]); 

    return (
        <div style={{ textAlign: "center" }}>
            <h2>Top Tracks</h2>

    
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
                    <option value="long_term">Last 12 months</option>
                </select>
            </div>

            {/* song list */}
            <div style={{ display: "flex", flexWrap: "wrap", justifyContent: "center" }}>
                {tracks.map((track, index) => (
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
                ))}
            </div>
        </div>
    );
}

export default TopTracks;
