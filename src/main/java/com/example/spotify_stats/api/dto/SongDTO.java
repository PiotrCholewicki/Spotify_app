package com.example.spotify_stats.api.dto;

import lombok.Data;

//class that has only the necessary fields for frontend
@Data
public class SongDTO {
    private String title;
    private String artist;
    private String songUrl;
    private String imageUrl;
    private String time_range;
}
