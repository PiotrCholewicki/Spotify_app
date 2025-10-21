package com.example.spotify_stats.api.service;

import com.example.spotify_stats.api.controller.ApiController;
import com.example.spotify_stats.api.dto.SongDTO;
import com.example.spotify_stats.api.persistence.entity.Song;
import com.example.spotify_stats.api.persistence.entity.UserDetails;
import org.junit.jupiter.api.Test;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTopTracksServiceTest {

    @Test
    void shouldBeTheSameObjects() {
        // given
        Song song = new Song();
        song.setSongName("Test Song");
        song.setArtist("Test Artist");
        song.setSpotifyUrl("https://open.spotify.com/track/test123");
        song.setImageUrl("https://image.test/img.jpg");
        song.setTime_range("long_term");

        UserTopTracksService userTopTracksService = new UserTopTracksService();
        SongDTO songDTO = userTopTracksService.mapSongToDTO(song);
        assertEquals(song.getSongName(), songDTO.getTitle());
        assertEquals(song.getSpotifyUrl(), songDTO.getSongUrl());
        assertEquals(song.getImageUrl(), songDTO.getImageUrl());
        assertEquals(song.getArtist(), songDTO.getArtist());
    }
}