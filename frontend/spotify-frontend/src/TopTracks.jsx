import React, { useEffect, useState } from "react";

function TopTracks({ userId }) {
    const [tracks, setTracks] = useState([]);

    useEffect(() => {
        if (!userId) return;

        fetch(`http://localhost:8080/api/user-top-songs?userId=${userId}`)
            .then(res => res.json())
            .then(data => setTracks(data))
            .catch(err => console.error(err));
    }, [userId]);

    return (

        <div>
            <h2 style={{ textAlign: "center" }}>Top Tracks</h2>
            <div style={{ display: "flex", flexWrap: "wrap" }}>
                {tracks.map((track, index) => (
                    <div key={index} style={{ margin: "10px", width: "200px", textAlign: "center" }}>
                        <a href={track.songUrl} target="_blank" rel="noopener noreferrer">
                           <img src={track.imageUrl} alt={track.title} style={{ width: "100%" }} />
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
