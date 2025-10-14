package com.example.spotify_stats.api.persistence.repository;

import com.example.spotify_stats.api.persistence.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Integer> {
    List<Song> findAllByUserRefId(String userRefId);
    void deleteAllByUserRefId(String userRefId);
    List<Song> findByUserRefIdAndArtistContainingIgnoreCase(String userId, String artist);

}
