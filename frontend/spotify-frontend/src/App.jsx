import React, { useState } from "react";
import Login from "./Login";
import TopTracks from "./TopTracks";

function App() {
    const [userId, setUserId] = useState(null);

    
    React.useEffect(() => {
        const params = new URLSearchParams(window.location.search);
        const id = params.get("userId");
        if (id) setUserId(id);
    }, []);

    return (
        <div>
            {!userId ? <Login /> : <TopTracks userId={userId} />}
        </div>
    );
}

export default App;
