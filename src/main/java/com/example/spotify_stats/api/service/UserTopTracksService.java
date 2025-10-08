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

    private SongDTO songDTO;

    //returns logged user's top tracks
    public Track[] fetchUserTopTracks(String userId){

        //its mandatory for every function in order to establish authentication with spotify
        UserDetails userDetails = userDetailsRepository.findByRefId(userId);
        SpotifyApi object = spotifyConfiguration.getSpotifyObject();


        object.setAccessToken(userDetails.getAccessToken());
        object.setRefreshToken(userDetails.getRefreshToken());

        final GetUsersTopTracksRequest getUsersTopTracksRequest = object.getUsersTopTracks()
                .time_range("short-term")
                .limit(50)
                .offset(0)
                .build();
        try{
            final Paging<Track> trackPaging = getUsersTopTracksRequest.execute();
            Track[] tracks = trackPaging.getItems();
            System.out.println("Fetched " + trackPaging.getTotal() + " top tracks for user " + userId);
            System.out.println(Arrays.toString(tracks));

            return trackPaging.getItems();
        }
        catch (Exception e){
            System.out.println("Exception occurred while fetching top songs" + e);

        }
        return new Track[0];
    }

    //it is used to map only the necessary fields to frontend
    public Song mapSpotifyTracksToSongs(Track track){
         Song song = new Song();
         song.setSongName(track.getName());
         song.setSpotifyUrl(track.getExternalUrls().get("spotify"));
         song.setImageUrl(track.getAlbum().getImages()[0].getUrl());

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
        songRepository.deleteAll();
        //download data from API
        Track[] topTracks = fetchUserTopTracks(userId);
        //add songs to database
        for(Track track : topTracks){
            Song song = mapSpotifyTracksToSongs(track);
            song.setUserRefId(userId);
            songRepository.save(song);
        }
        //read all the songs from the database
        return songRepository.findAllByUserRefId(userId);
    }
    public List<SongDTO> getSongDTO(String userId, String time_range){
        List<SongDTO> topSongsDTO = new ArrayList<SongDTO>();
        List<Song> userSongs = songRepository.findAllByUserRefId(userId);
        for(Song song : userSongs){
            //we export only the necessary info to the frontend
            SongDTO smallerSong = new SongDTO();
            smallerSong.setArtist(song.getArtist());
            smallerSong.setTitle(song.getSongName());
            smallerSong.setSongUrl(song.getSpotifyUrl());
            smallerSong.setImageUrl(song.getImageUrl());
            smallerSong.setTime_range(time_range);
            topSongsDTO.add(smallerSong);
        }
        return topSongsDTO;
    }

}
