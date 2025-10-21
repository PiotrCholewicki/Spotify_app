package com.example.spotify_stats.api.service;

import com.example.spotify_stats.api.config.SpotifyConfiguration;
import com.example.spotify_stats.api.dto.SongDTO;
import com.example.spotify_stats.api.persistence.entity.Song;
import com.example.spotify_stats.api.persistence.entity.UserDetails;
import com.example.spotify_stats.api.persistence.repository.SongRepository;
import com.example.spotify_stats.api.persistence.repository.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.data.personalization.simplified.GetUsersTopTracksRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
@Service
public class UserTopTracksService {
    @Autowired
    private SongRepository songRepository;
    @Autowired
    private UserDetailsRepository userDetailsRepository;

    @Autowired
    private SpotifyConfiguration spotifyConfiguration;


    //returns logged user's top tracks
    public Track[] fetchUserTopTracks(String userId, String time_range, int offset){

        //its mandatory for every function in order to establish authentication with spotify
        UserDetails userDetails = userDetailsRepository.findByRefId(userId);
        SpotifyApi object = spotifyConfiguration.getSpotifyObject();


        object.setAccessToken(userDetails.getAccessToken());
        object.setRefreshToken(userDetails.getRefreshToken());

        final GetUsersTopTracksRequest getUsersTopTracksRequest = object.getUsersTopTracks()
                .time_range(time_range)
                .limit(50)
                .offset(offset)
                .build();

        try{
            final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
            Track[] tracks = trackPaging.getItems();
            //System.out.println("Fetched " + trackPaging.getTotal() + " top tracks for user " + userId);
            //System.out.println(Arrays.toString(tracks));

            return trackPaging.getItems();
        }
        catch (Exception e){
            System.out.println("Exception occurred while fetching top songs" + e);

        }
        return new Track[0];
    }
    //if no offset is given call with "default" parameter = 0
    public Track[] fetchUserTopTracks(String userId, String time_range) {
        return fetchUserTopTracks(userId, time_range, 0);
    }
    //it is used to map only the necessary fields to frontend
    public Song mapSpotifyTracksToSongs(Track track){
         Song song = new Song();
         song.setSongName(track.getName());
         song.setSpotifyUrl(track.getExternalUrls().get("spotify"));
        if(track.getAlbum().getImages() != null && track.getAlbum().getImages().length > 0)
        {
            song.setImageUrl(track.getAlbum().getImages()[0].getUrl());
        }
        else{
            song.setImageUrl(null);
        }

         if(track.getArtists().length > 1){
             StringBuilder artistList = new StringBuilder();
             for(int i = 0; i < track.getArtists().length; i++){
                 artistList.append(track.getArtists()[i].getName());
                 if (i < track.getArtists().length - 1) {
                     artistList.append(", ");
                 }
             }
             song.setArtist(artistList.toString());
         }
         else{
            song.setArtist(track.getArtists()[0].getName());
         }
        return song;
    }
    public List<Song> addTopSongsToDatabase(String userId){
        //first delete all the data from the past
        songRepository.deleteAllByUserRefId(userId);
        //download data from API

        for(int i = 0; i < 250; i+=50){
            Track[] topTracks = fetchUserTopTracks(userId, "long_term", i);
            if(topTracks.length == 0) break; // no more results
            //add songs to database
            for(Track track : topTracks){
                Song song = mapSpotifyTracksToSongs(track);
                song.setTime_range("long_term");
                song.setUserRefId(userId);
                songRepository.save(song);
                System.out.println("Inserting song into database");
            }
        }


        //read all the songs from the database
        songRepository.findAllByUserRefId(userId);
        return songRepository.findAllByUserRefId(userId);
    }
    public List<SongDTO> getSongDTO(String userId, String time_range){
        List<SongDTO> topSongsDTO = new ArrayList<>();
        if(Objects.equals(time_range, "long_term")){
            //fetch 1000 results if long term
            if(songRepository.findAllByUserRefId(userId).isEmpty()){
                addTopSongsToDatabase(userId);
            }
            List<Song> songList = songRepository.findAllByUserRefId(userId);
            List<SongDTO> songDTOs = new ArrayList<>();
            for(Song song : songList){
                songDTOs.add(mapSongToDTO(song));
            }
            return songDTOs;

        }
        else{
            //fetch first 50 results if medium or short term
            Track[] tracks = fetchUserTopTracks(userId, time_range, 0);
            for(Track song: tracks){
                //we export only the necessary info to the frontend, thats why we use DTO
                SongDTO smallerSong = new SongDTO();
                StringBuilder artists = new StringBuilder();
                for(int i = 0; i < song.getArtists().length; i++){
                    artists.append(song.getArtists()[i].getName());
                    if(i != song.getArtists().length - 1){
                        artists.append(", ");
                    }
                }
                smallerSong.setArtist(artists.toString());

                smallerSong.setTitle(song.getName());
                smallerSong.setImageUrl(song.getAlbum().getImages()[0].getUrl());
                smallerSong.setSongUrl(song.getExternalUrls().get("spotify"));
                smallerSong.setTime_range(time_range);
                topSongsDTO.add(smallerSong);
            }
        }
        return topSongsDTO;
    }
    public SongDTO mapSongToDTO(Song song){
        SongDTO songDTO = new SongDTO();
        songDTO.setSongUrl(song.getSpotifyUrl());
        songDTO.setArtist(song.getArtist());
        songDTO.setTitle(song.getSongName());
        songDTO.setTime_range(song.getTime_range());
        songDTO.setImageUrl(song.getImageUrl());
        return songDTO;
    }
    public List<SongDTO> getUserTopTracksByArtist(String userId, String artist) {
        List<Song> allSongs = songRepository.findByUserRefIdAndArtistContainingIgnoreCase(userId, artist);

        // if no data, save it to API
        if (allSongs.isEmpty()) {
            addTopSongsToDatabase(userId);

            allSongs = songRepository.findByUserRefIdAndArtistContainingIgnoreCase(userId, artist);
        }
        //declarative method to map all the songs at once to DTO
        return allSongs.stream().map(this::mapSongToDTO).toList();
    }
}
