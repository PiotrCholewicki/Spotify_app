package com.example.spotify_stats.api.persistence.repository;

import com.example.spotify_stats.api.persistence.entity.TopArtist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<TopArtist, Integer> {

    List<TopArtist> findAllByUserRefId(String userRefId);

    void deleteAllByUserRefId(String userId);

}
