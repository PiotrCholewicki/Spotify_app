package com.example.spotify_stats.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

/// this service is used to establish connection with spotify api, it is used in controller
@Service
public class SpotifyConfiguration {
    @Value("${redirect.server.ip}")
    private String customIp;

    @Value("${app.api.key}")
    private String clientId;

    @Value("${app.api.secret}")
    private String clientSecret;

    // creates new spotifyApi object for each session
    public SpotifyApi createSpotifyApi() {
        URI redirectUri = SpotifyHttpManager.makeUri(customIp + "/api/get-user-code");
        return new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectUri)
                .build();
    }

    public SpotifyApi getSpotifyObject(){
        URI redirectedURL = SpotifyHttpManager.makeUri(customIp + "/api/get-user-code");
        return new SpotifyApi
                .Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRedirectUri(redirectedURL)
                .build();
    }

}
