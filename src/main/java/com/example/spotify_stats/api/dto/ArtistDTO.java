package com.example.spotify_stats.api.dto;


import lombok.Data;

@Data
public class ArtistDTO {
    private String name;

    private String spotifyUrl;

    private String imageUrl;
}
