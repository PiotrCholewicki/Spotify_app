package com.example.spotify_stats.api.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "ARTIST")
@Data
public class TopArtist {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private String userRefId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SPOTIFY_URL")
    private String spotifyUrl;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "TIME_RANGE")
    private String time_range;
}
