package com.example.spotify_stats.api.service;

import com.example.spotify_stats.api.config.SpotifyConfiguration;
import com.example.spotify_stats.api.dto.ArtistDTO;
import com.example.spotify_stats.api.dto.SongDTO;
import com.example.spotify_stats.api.persistence.entity.Song;
import com.example.spotify_stats.api.persistence.entity.TopArtist;
import com.example.spotify_stats.api.persistence.entity.UserDetails;
import com.example.spotify_stats.api.persistence.repository.ArtistRepository;
import com.example.spotify_stats.api.persistence.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Artist;

import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopArtistsRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserTopArtistsService
{
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private SpotifyConfiguration spotifyConfiguration;

    public Artist[] fetchUserTopArtists(String userId, String time_range, int offset){
        //its mandatory for every function in order to establish authentication with spotify
        UserDetails userDetails = userDetailsRepository.findByRefId(userId);
        SpotifyApi object = spotifyConfiguration.getSpotifyObject();


        object.setAccessToken(userDetails.getAccessToken());
        object.setRefreshToken(userDetails.getRefreshToken());

        final GetUsersTopArtistsRequest getUsersTopArtistsRequest = object.getUsersTopArtists()
                .time_range(time_range)
                .offset(offset)
                .limit(50)
                .build();

        try{
            final Paging<Artist> artistPaging = getUsersTopArtistsRequest.execute();
            Artist[] artists = artistPaging.getItems();


            return artistPaging.getItems();
        }
        catch (Exception e){
            System.out.println("Exception occurred while fetching top songs" + e);

        }
        return new Artist[0];
    }
    //it is used to map only the necessary fields to frontend
    public TopArtist mapSpotifyArtistToTopArtist(Artist artist){
        TopArtist topArtist = new TopArtist();
        topArtist.setName(artist.getName());
        topArtist.setSpotifyUrl(artist.getExternalUrls().get("spotify"));

        // safe check if there is any image
        if (artist.getImages() != null && artist.getImages().length > 0) {
            topArtist.setImageUrl(artist.getImages()[0].getUrl());
        } else {
            topArtist.setImageUrl(null);
        }

        return topArtist;
    }
    public List<TopArtist> addArtistsToDatabase(String userId) {
        artistRepository.deleteAllByUserRefId(userId);
        //load 500 artists

        for (int offset = 0; offset < 200; offset += 50) {
            Artist[] artists = fetchUserTopArtists(userId, "long_term", offset);

            for (Artist artist : artists) {
                TopArtist topArtist = mapSpotifyArtistToTopArtist(artist);

                topArtist.setUserRefId(userId);
                topArtist.setTime_range("long_term");
                //adding to database
                artistRepository.save(topArtist);

            }
        }
        artistRepository.findAllByUserRefId(userId);
        return artistRepository.findAllByUserRefId(userId);
    }
    public List <ArtistDTO> getArtistDTO(String userId, String time_range){
        List<ArtistDTO> artistDTOs = new ArrayList<>();
        if(Objects.equals(time_range, "long_term")){
            //if long term get data from database, else just fetch straight from the api

            if(artistRepository.findAllByUserRefId(userId).isEmpty()){
                addArtistsToDatabase(userId);
                artistRepository.findAllByUserRefId(userId);
            }
            List<TopArtist> artists = artistRepository.findAllByUserRefId(userId);
            for(TopArtist topArtist : artists){
                artistDTOs.add(mapArtistToDTO(topArtist));
            }
        }
        else{
            Artist[] artists = fetchUserTopArtists(userId, time_range, 0);
            if (artists.length == 0) {
                return artistDTOs;
            }
            for(Artist artist : artists){
                ArtistDTO artistDTO = mapArtistToDTO(mapSpotifyArtistToTopArtist(artist));
                artistDTOs.add(artistDTO);
            }
        }

        return artistDTOs;


    }
    public ArtistDTO mapArtistToDTO(TopArtist topArtist){
        ArtistDTO artistDTO = new ArtistDTO();
        artistDTO.setName(topArtist.getName());
        artistDTO.setSpotifyUrl(topArtist.getSpotifyUrl());
        artistDTO.setImageUrl(topArtist.getImageUrl());
        return artistDTO;
    }


}
