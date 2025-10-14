package com.example.spotify_stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
public class SpotifyStatsApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpotifyStatsApplication.class, args);
    }
}
